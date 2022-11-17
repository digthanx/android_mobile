package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.databinding.FragmentSettingsBinding
import com.teamforce.thanksapp.presentation.viewmodel.profile.ProfileViewModel
import com.teamforce.thanksapp.presentation.viewmodel.profile.SettingsViewModel
import com.teamforce.thanksapp.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding: FragmentSettingsBinding by viewBinding()

    private val viewModel: SettingsViewModel by viewModels()
    private val sharedViewModel: NotificationSharedViewModel by activityViewModels()



    private var listOfOrg: MutableList<OrganizationModel> = mutableListOf()
    var adapter: ArrayAdapter<String>? = null


    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding.orgFilterSpinner.setAdapter(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestData()
        initAdapter()
        setData()
        listeners()

        binding.profile.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.notifyLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_notificationFragment)
        }

        sharedViewModel.state.observe(viewLifecycleOwner) { notificationsCount ->
            if (notificationsCount == 0) {
                binding.apply {
                    activeNotifyLayout.invisible()
                    notify.visible()
                }
            } else {
                binding.apply {
                    activeNotifyLayout.visible()
                    notify.invisible()
                    notifyBadge.text = notificationsCount.toString()
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.orgFilterSpinner.setAdapter(adapter)
    }

    private fun listeners() {
        viewModel.authResult.observe(viewLifecycleOwner) {

            if (viewModel.xId != null && viewModel.xCode != null && viewModel.orgCode != null) {
                sendToastAboutVerifyCode()
                viewModel.saveCredentialsForChangeOrg()
                if (it) activityNavController().navigateSafely(
                    R.id.action_global_signFlowFragment,
                    null,
                    OptionsTransaction().optionForTransaction
                )
            }
        }

        binding.orgFilterSpinner.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                adapter?.let { showAlertDialogForChangeOrg(it, id) }
            }
    }

    private fun requestData() {
        viewModel.loadUserOrganizations()
    }

    private fun setData() {

        viewModel.organizations.observe(viewLifecycleOwner) {
            it?.let {
                listOfOrg.clear()
                adapter?.clear()
                it.forEach { orgModel ->
                    adapter?.add(orgModel.name)
                    listOfOrg.add(orgModel)
                    if (orgModel.is_current) {
                        binding.orgFilterSpinner.hint = (orgModel.name)
                        binding.orgFilterContainer.hint =
                            requireContext().getString(R.string.currentOrganisation)
                        binding.orgFilterContainer.requestFocus()
                    }
                }
                Log.d(ProfileFragment.TAG, "Size adapter ${adapter?.count}")
                Log.d(ProfileFragment.TAG, "Size listOfOrg ${listOfOrg.size}")
            }
        }
    }

    private fun showAlertDialogForChangeOrg(adapter: ArrayAdapter<String>, id: Long) {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.wouldYouLikeToChangeOrg))

            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                binding.orgFilterSpinner.setText("")
                // Сбросить выделение после отказа
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                dialog.cancel()
                if (listOfOrg.size > 0) {
                    viewModel.changeOrg(listOfOrg[id.toInt()].id)
                }

            }
            .show()
    }

    private fun sendToastAboutVerifyCode() {
        if (viewModel.authorizationType is ProfileViewModel.AuthorizationType.Telegram) {
            Toast.makeText(
                requireContext(),
                R.string.Toast_verifyCode_hintTg,
                Toast.LENGTH_LONG
            ).show()

        } else {
            Toast.makeText(
                requireContext(),
                R.string.Toast_verifyCode_hintEmail,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SettingsFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}