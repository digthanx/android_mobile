package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentCreateReportBinding
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.utils.OptionsTransaction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateReportFragment : Fragment(R.layout.fragment_create_report) {

    private val binding: FragmentCreateReportBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBtn.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}