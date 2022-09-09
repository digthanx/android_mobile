package com.teamforce.thanksapp.presentation.fragment.newTransactionScreen

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentListOfValuesBinding
import com.teamforce.thanksapp.presentation.adapter.ValuesAdapter
import com.teamforce.thanksapp.presentation.viewmodel.ListOfValuesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListOfValuesFragment : DialogFragment() {

    private var _binding: FragmentListOfValuesBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val recyclerView: RecyclerView by lazy { binding.valuesRv }
    private val applyValuesBtn: MaterialButton by lazy { binding.addValuesBtn }
    private val progressBar: ProgressBar by lazy { binding.progressBar }
    private val viewModel: ListOfValuesViewModel by viewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfValuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        showLoadingProgress()
        setDataFromServer()
        applyValuesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listOfValuesFragment4_to_transactionFragment)
        }
    }

    private fun loadData() {
        viewModel.userDataRepository.token?.let { token ->
            viewModel.loadTags(token)
        }
    }

    private fun showLoadingProgress() {
        viewModel.isLoading.observe(
            viewLifecycleOwner
        ) { isLoading ->
            if (isLoading) {
                recyclerView.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun setDataFromServer(){
        viewModel.tags.observe(
            viewLifecycleOwner
        ) {
            recyclerView.visibility = View.VISIBLE
            recyclerView.adapter = ValuesAdapter(it, requireContext())
        }
    }


}