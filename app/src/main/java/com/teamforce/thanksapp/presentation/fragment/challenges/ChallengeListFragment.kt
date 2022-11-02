package com.teamforce.thanksapp.presentation.fragment.challenges

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamforce.thanksapp.R
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class ChallengeListFragment : Fragment(R.layout.fragment_challenge_list) {

    private var activeOnly: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            activeOnly = it.getInt(ACTIVE_ONLY)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    companion object {
        private const val ACTIVE_ONLY = "active_only"

        @JvmStatic
        fun newInstance(activeOnly: Int) =
            ChallengeListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ACTIVE_ONLY, activeOnly)
                }
            }
    }
}