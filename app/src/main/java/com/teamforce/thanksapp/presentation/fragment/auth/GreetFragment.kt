package com.teamforce.thanksapp.presentation.fragment.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentGreetBinding
import com.teamforce.thanksapp.databinding.FragmentTransactionResultBinding


class GreetFragment : Fragment() {


    private var _binding: FragmentGreetBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGreetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_greetFragment_to_loginFragment)
        }
    }

}