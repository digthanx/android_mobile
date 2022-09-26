package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentSomeonesProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.SomeonesProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SomeonesProfileFragment : Fragment(R.layout.fragment_someones_profile) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentSomeonesProfileBinding by viewBinding()

    private val viewModel: SomeonesProfileViewModel by viewModels()

    private var userId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("userId")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.balanceFragment,
                R.id.feedFragment,
                R.id.historyFragment
            )
        )
        binding.toolbarTransaction.setupWithNavController(navController, appBarConfiguration)
        initViews()
        requestData()
        setData()
        swipeToRefresh()
    }

    private fun initViews() {
        binding.swipeRefreshLayout.setColorSchemeColors(requireContext().getColor(R.color.general_brand))
        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    binding.header.visibility = View.GONE
                    binding.information.visibility = View.GONE
                    binding.placeJob.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.header.visibility = View.VISIBLE
                    binding.information.visibility = View.VISIBLE
                    binding.placeJob.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }
        )
    }

    private fun swipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            requestData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun requestData() {
        val token = viewModel.userDataRepository.token
        if (token != null) {
            userId?.let {
                viewModel.loadAnotherUserProfile(token, it)
            }
        }
    }

    private fun setData() {
        viewModel.anotherProfile.observe(viewLifecycleOwner) {
            binding.firstNameValueTv.text = it.profile.firstname
            binding.surnameValueTv.text = it.profile.surname
            binding.middleNameValueTv.text = it.profile.middlename
            binding.companyValueTv.text = it.profile.organization
            binding.positionValueTv.text = it.profile.jobTitle
            binding.userTgName.text = it.profile.tgName
            if (it.profile.contacts.size == 1) {
                if (it.profile.contacts[0].contact_type == "@") {
                    binding.emailValueTv.text = it.profile.contacts[0].contact_id
                } else {
                    binding.mobileValueTv.text = it.profile.contacts[0].contact_id
                }
            } else if (it.profile.contacts.size == 2) {
                if (it.profile.contacts[0].contact_type == "@") {
                    binding.emailValueTv.text = it.profile.contacts[0].contact_id
                    binding.mobileValueTv.text = it.profile.contacts[1].contact_id
                } else {
                    binding.emailValueTv.text = it.profile.contacts[1].contact_id
                    binding.mobileValueTv.text = it.profile.contacts[0].contact_id
                }
            }

            if (!it.profile.photo.isNullOrEmpty()) {
                Glide.with(this)
                    .load("${Consts.BASE_URL}${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(binding.userAvatar)
            } else {
                binding.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }
        }
    }
}