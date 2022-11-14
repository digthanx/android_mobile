package com.teamforce.thanksapp.presentation.fragment.feedScreen.fragmentsViewPager2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.data.entities.feed.Tag
import com.teamforce.thanksapp.databinding.FragmentDetailsInnerFeedBinding
import com.teamforce.thanksapp.presentation.viewmodel.feed.DetailInnerFeedViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.Consts.TRANSACTION_ID
import com.teamforce.thanksapp.utils.showDialogAboutDownloadImage
import com.teamforce.thanksapp.utils.viewSinglePhoto
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [DetailsInnerFeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class DetailsInnerFeedFragment : Fragment(R.layout.fragment_details_inner_feed) {

    private val binding: FragmentDetailsInnerFeedBinding by viewBinding()

    private val viewModel: DetailInnerFeedViewModel by viewModels()

    private var transactionId: Int? = null

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
            transactionId = it.getInt(TRANSACTION_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionId?.let { viewModel.loadTransactionDetail(it) }
        setDetail()

    }

    private fun setDetail(){
        viewModel.dataOfTransaction.observe(viewLifecycleOwner){
            with(binding){
                if(it?.reason != "") {
                    messageCard.visibility = View.VISIBLE
                    reasonTransaction.text = it?.reason
                }else messageCard.visibility = View.GONE

                if (it != null) {
                    setTags(it.tags)
                }
                if(it?.photo != null){
                    photoTv.visibility = View.VISIBLE
                    cardViewImg.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .load("${Consts.BASE_URL}${it.photo}".toUri())
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.senderImage)
                }else{
                    photoTv.visibility = View.GONE
                    cardViewImg.visibility = View.GONE
                }

                binding.senderImage.setOnClickListener { view ->
                    it?.photo?.let { photo ->
                        (view as ImageView).viewSinglePhoto(photo, requireContext())
                    }
                }

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    binding.senderImage.setOnLongClickListener { view ->
                        it?.photo?.let { photo ->
                            showDialogAboutDownloadImage(photo, view, requireContext(), lifecycleScope)
                        }
                        return@setOnLongClickListener true
                    }
                }else {
                    if (checkPermission()) {
                        binding.senderImage.setOnLongClickListener { view ->
                            it?.photo?.let { photo ->
                                showDialogAboutDownloadImage(
                                    photo,
                                    view,
                                    requireContext(),
                                    lifecycleScope
                                )
                            }
                            return@setOnLongClickListener true
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            requireContext().getString(R.string.dontHaveEnoughPermissions),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    private fun setTags(tagList: List<Tag>) {
        for (i in tagList.indices) {
            val tagName = tagList[i].name
            val chip: Chip = LayoutInflater.from(binding.tagsChipGroup.context)
                .inflate(
                    R.layout.chip_tag_example_in_history_transaction,
                    binding.tagsChipGroup,
                    false
                ) as Chip
            with(chip) {
                setText(tagName)
                setEnsureMinTouchTargetSize(true)
                minimumWidth = 0
            }
            binding.tagsChipGroup.addView(chip)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(transactionId: Int) =
            DetailsInnerFeedFragment().apply {
                arguments = Bundle().apply {
                    putInt(TRANSACTION_ID, transactionId)
                }
            }
    }
}