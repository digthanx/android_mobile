package com.teamforce.thanksapp.presentation.fragment.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.teamforce.thanksapp.NotificationSharedViewModel
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentNotificationsBinding
import com.teamforce.thanksapp.presentation.viewmodel.NotificationsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private val binding: FragmentNotificationsBinding by viewBinding()

    private val viewModel: NotificationsViewModel by viewModels()
    private val sharedViewModel: NotificationSharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.dropNotificationCounter()

//        binding.apply {
//            closeButton.setOnClickListener {
//                findNavController().navigateUp()
//            }
//        }
    }
}