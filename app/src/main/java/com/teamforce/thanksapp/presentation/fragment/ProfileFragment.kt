package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.utils.UserDataRepository

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
    }

    private fun initViews(view: View) {
        val usernameTextView: TextView = view.findViewById(R.id.username_value_tv)
        usernameTextView.text = UserDataRepository.getInstance()?.username

        val tgIdTextView: TextView = view.findViewById(R.id.tg_id_value_tv)
        tgIdTextView.text = UserDataRepository.getInstance()?.tgId
    }

    companion object {

        const val TAG = "ProfileFragment"

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
