package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentMainFlowBinding
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.activityNavController
import com.teamforce.thanksapp.utils.navigateSafely

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        val userTgName = binding.userNick
        val userId = binding.userId
        val userAvatar = binding.userAvatar

        val userName = binding.usernameValueTv
        val legalFace = binding.legalEntityValueTv
        val departmentUser = binding.departmentValueTv
        val hiredAt = binding.hiredValueTv
        val mobileUser = binding.mobileValueTv
        val emailuser = binding.emailValueTv
        val viewModel = ProfileViewModel()
        viewModel.initViewModel()

        val token = UserDataRepository.getInstance()?.token
        if (token != null) {
            viewModel.loadUserProfile(token)
        }
        viewModel.profile.observe(viewLifecycleOwner){
            userTgName.text = it.profile.tgName
            userId.text = "id ${it.profile.tgId}"

            userName.text = it.username
            legalFace.text = it.profile.organization
            departmentUser.text = it.profile.department
            hiredAt.text = it.profile.hiredAt
            // Телефона пока нет
            // Почты тоже нет
        }

        binding.btnLogout.setOnClickListener {
            UserDataRepository.getInstance()?.logout(requireContext())
            activityNavController().navigateSafely(R.id.action_global_signFlowFragment)
        }
    }

    private fun initViews(view: View) {

    }

    companion object {

        const val TAG = "ProfileFragment"

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
