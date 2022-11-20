package com.teamforce.thanksapp.presentation.fragment.challenges.fragmentsViewPager2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMyResultChallengeBinding
import com.teamforce.thanksapp.presentation.adapter.challenge.ResultChallengeAdapter
import com.teamforce.thanksapp.presentation.fragment.challenges.ChallengesConsts.CHALLENGER_ID
import com.teamforce.thanksapp.presentation.viewmodel.challenge.MyResultChallengeViewModel
import com.teamforce.thanksapp.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyResultChallengeFragment : Fragment(R.layout.fragment_my_result_challenge) {

    private val binding: FragmentMyResultChallengeBinding by viewBinding()

    private val viewModel: MyResultChallengeViewModel by viewModels()

    private var idChallenge: Int? = null
    private var listAdapter: ResultChallengeAdapter? = null


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
            idChallenge = it.getInt(CHALLENGER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = ResultChallengeAdapter()
        binding.listOfResults.adapter = listAdapter
        loadMyResult()
        listeners()

        listAdapter?.onImageClicked = { clickedView, photo ->
            (clickedView as ShapeableImageView).imageView(
                photo,
                requireContext(),
                PosterOverlayView(requireContext()) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val url = "${Consts.BASE_URL}${photo.replace("_thumb", "")}"
                            downloadImage(url, requireContext())
                        } else {
                            when {
                                ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    val url = "${Consts.BASE_URL}${photo.replace("_thumb", "")}"
                                    downloadImage(url, requireContext())
                                }
                                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                                    showRequestPermissionRational()
                                }
                                else -> {
                                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    private fun loadMyResult() {
        idChallenge?.let {
            viewModel.loadChallengeResult(it)
        }
    }

    private fun listeners() {
        viewModel.myResultError.observe(viewLifecycleOwner) {
            binding.noData.visibility = View.VISIBLE
        }

        viewModel.myResult.observe(viewLifecycleOwner) {
            (binding.listOfResults.adapter as ResultChallengeAdapter).submitList(it?.reversed())
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(challengeId: Int) =
            MyResultChallengeFragment().apply {
                arguments = Bundle().apply {
                    putInt(CHALLENGER_ID, challengeId)
                }
            }
    }
}