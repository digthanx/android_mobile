package com.teamforce.thanksapp.presentation.fragment.feedScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoFeedItemBinding
import com.teamforce.thanksapp.databinding.FragmentAdditionalInfoTransactionBottomSheetBinding


class AdditionalInfoFeedItemFragment : Fragment() {

    private var _binding: FragmentAdditionalInfoFeedItemBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdditionalInfoFeedItemBinding.inflate(inflater, container, false)
        return binding.root
    }


}