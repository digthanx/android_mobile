package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.databinding.FragmentContendersChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.ContendersAdapter
import com.teamforce.thanksapp.presentation.adapter.decorators.VerticalDividerItemDecorator
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesFragment.Companion.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.viewmodel.ContendersChallengeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContendersChallengeFragment : Fragment(R.layout.fragment_contenders_challenge) {

    private val binding: FragmentContendersChallengeBinding by viewBinding()

    private val viewModel: ContendersChallengeViewModel by viewModels()

    private var idChallenge: Int? = null
    private var listOfContenders: MutableList<GetChallengeContendersResponse.Contender> =
        mutableListOf()

    private var contendersAdapter: ContendersAdapter? = null

    private var currentPositionItem = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onDestroyView() {
        binding.contendersRv.adapter = null
        contendersAdapter = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contendersAdapter =
            ContendersAdapter(applyClickListener = ::apply, refuseClickListener = ::refuse)
        binding.contendersRv.adapter = contendersAdapter
        binding.contendersRv.addItemDecoration(
            VerticalDividerItemDecorator(
                16,
                contendersAdapter!!.itemCount
            )
        )
        loadParticipants()
        setData()

        viewModel.contendersError.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        viewModel.isSuccessOperation.observe(viewLifecycleOwner) {
            if (it.successResult)
                if (it.state == 'W') {
                    listOfContenders.removeAt(currentPositionItem)
                    contendersAdapter?.submitList(null)
                    contendersAdapter?.submitList(listOfContenders)
                    Toast.makeText(
                        requireContext(), requireContext().getString(R.string.applyCheckReport),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    listOfContenders.removeAt(currentPositionItem)
                    Log.d(TAG, "Размер списка в фрагменте " + listOfContenders.size.toString())
                    contendersAdapter?.submitList(null)
                    contendersAdapter?.submitList(listOfContenders)
                    Log.d(
                        TAG,
                        "listeningResponse: размер списка adapter.currentList " + contendersAdapter?.currentList?.size
                    )
                    contendersAdapter?.notifyDataSetChanged()
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.deniedCheckReport),
                        Toast.LENGTH_LONG
                    ).show()
                }

        }
    }

    private fun loadParticipants() {
        idChallenge?.let { viewModel.loadContenders(it) }
    }

    private fun setData() {
        viewModel.contenders.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.noData.visibility = View.GONE
                listOfContenders = it.toMutableList()
                Log.d(TAG, "setData: setdata listOfContenders.size = " + listOfContenders.size)
                contendersAdapter?.submitList(it.toMutableList())
            } else {
                binding.noData.visibility = View.VISIBLE
            }
        }
    }

    private fun createDialog(
        reportId: Int,
        state: Char,
    ) {
        val builderDialog = AlertDialog.Builder(context, R.style.FullscreenDialogTheme)
        val inflater = requireActivity().layoutInflater
        val newListValues = inflater.inflate(R.layout.dialog_reason_for_rejection_report, null)
        builderDialog.setView(newListValues)
        val dialog = builderDialog.create()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        );

        dialog.show()

        val refuseBtn = dialog.findViewById<MaterialButton>(R.id.reject_btn)
        val closeDialogBtn = dialog.findViewById<MaterialButton>(R.id.closeDialog_btn)
        refuseBtn.setOnClickListener {
            if (dialog.findViewById<TextInputEditText>(R.id.description_et)
                    .text?.trim()?.isNotEmpty() == true
            ) {
                viewModel.checkReport(
                    reportId, state,
                    dialog.findViewById<TextInputEditText>(R.id.description_et).text.toString()
                )
                dialog.cancel()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.reasonOfRejectionIsNecessary),
                    Toast.LENGTH_LONG
                ).show()
            }

        }
        closeDialogBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    companion object {
        public const val TAG = "ContendersChallengeFragment"

        @JvmStatic
        fun newInstance(challengeId: Int) =
            ContendersChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHALLENGER_ID, challengeId)
                }
            }
    }

    private fun apply(reportId: Int, state: Char, position: Int) {
        currentPositionItem = position
        viewModel.checkReport(reportId, state, " ")
    }

    private fun refuse(reportId: Int, state: Char, position: Int) {
        Log.d(TAG, "onViewCreated: refused item index $position")
        currentPositionItem = position
        createDialog(reportId, state)
    }


}