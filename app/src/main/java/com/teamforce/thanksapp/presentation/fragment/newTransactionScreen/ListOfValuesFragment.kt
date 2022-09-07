package com.teamforce.thanksapp.presentation.fragment.newTransactionScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentListOfValuesBinding
import com.teamforce.thanksapp.databinding.FragmentTransactionBinding


class ListOfValuesFragment : Fragment() {

    private var _binding: FragmentListOfValuesBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    private val recyclerView: RecyclerView by lazy { binding.valuesRv }
    private val applyValuesBtn: MaterialButton by lazy { binding.addValuesBtn }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListOfValuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyValuesBtn.setOnClickListener {
            findNavController().navigate(R.id.action_listOfValuesFragment_to_transactionFragment)
        }
    }


}