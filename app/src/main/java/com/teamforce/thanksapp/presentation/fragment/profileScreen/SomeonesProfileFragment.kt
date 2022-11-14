package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentSomeonesProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.SomeonesProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.showDialogAboutDownloadImage
import com.teamforce.thanksapp.utils.viewSinglePhoto
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SomeonesProfileFragment : Fragment(R.layout.fragment_someones_profile) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentSomeonesProfileBinding by viewBinding()

    private val viewModel: SomeonesProfileViewModel by viewModels()

    private var userId: Int? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                showDialogAboutPermissions()

            }
        }

    private fun showDialogAboutPermissions() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.explainingAboutPermissions))

            .setNegativeButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.settings)) { dialog, which ->
                dialog.cancel()
                val reqIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .apply {
                        val uri = Uri.fromParts("package", "com.teamforce.thanksapp", null)
                        data = uri
                    }
                startActivity(reqIntent)
                // Почему то повторно не запрашивается разрешение
                // requestPermissions()
            }
            .show()
    }

    private fun checkPermission(): Boolean {

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                return true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                showRequestPermissionRational()
                return false
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return false
            }
        }
    }

    private fun showRequestPermissionRational() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.explainingAboutPermissionsRational))

            .setNegativeButton(resources.getString(R.string.close)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(resources.getString(R.string.good)) { dialog, _ ->
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                dialog.cancel()
            }
            .show()
    }


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
        userId?.let {
            viewModel.loadAnotherUserProfile(it)
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
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.userAvatar)
            } else {
                binding.userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }
            binding.userAvatar.setOnClickListener { view ->
                it.profile.photo?.let { photo ->
                    (view as ShapeableImageView).viewSinglePhoto(photo, requireContext())
                }
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                binding.userAvatar.setOnLongClickListener { view ->
                    it.profile.photo?.let { photo ->
                        showDialogAboutDownloadImage(photo, view, requireContext(), lifecycleScope)
                    }
                    return@setOnLongClickListener true
                }
            }else{
                if(checkPermission()){
                    binding.userAvatar.setOnLongClickListener { view ->
                        it.profile.photo?.let { photo ->
                            showDialogAboutDownloadImage(photo, view, requireContext(), lifecycleScope)
                        }
                        return@setOnLongClickListener true
                    }
                }else{
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.dontHaveEnoughPermissions),
                        Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}