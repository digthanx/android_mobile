package com.teamforce.thanksapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private val viewModel = ProfileViewModel()

    private lateinit var userTgName: TextView
    private lateinit var userAvatar: ImageView
    private lateinit var userFio: TextView

    private lateinit var userEmail: TextView
    private lateinit var userPhone: TextView

    private lateinit var companyUser: TextView
    private lateinit var departmentUser: TextView
    private lateinit var hiredAt: TextView

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
        initViews()
        requestData()
        setData()

    }

     private fun requestData(){
         val token = UserDataRepository.getInstance()?.token
         if (token != null) {
             viewModel.loadUserProfile(token)
         }
     }

    private fun setData(){
        viewModel.profile.observe(viewLifecycleOwner){
            userFio.text = String.format(requireContext().getString(R.string.userFio),
                it.profile.surname ,it.profile.firstname, it.profile.middlename)

            userTgName.text = "@" + it.profile.tgName

            if(it.profile.contacts.size == 1){
                userEmail.text = it.profile.contacts[0].contact_id
            }else if(it.profile.contacts.size == 2){
                userEmail.text = it.profile.contacts[0].contact_id
                userPhone.text = it.profile.contacts[1].contact_id
            }


            companyUser.text = it.profile.organization
            departmentUser.text = it.profile.department
            hiredAt.text = it.profile.hiredAt
            // Пока не знаю как изображение подгружать и как с ними работать
        }

        binding.exitBtn.setOnClickListener {
            showAlertDialogForExit()
        }
    }

    private fun showAlertDialogForExit(){
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.wouldYouLikeToExit))

            .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                dialog.cancel()
                UserDataRepository.getInstance()?.logout(requireContext())
                activityNavController().navigateSafely(R.id.action_global_signFlowFragment)
            }
            .show()
    }

    private fun initViews() {
        userTgName = binding.userTelegramId
        userAvatar = binding.userAvatar
        userFio = binding.userFio

        userEmail = binding.emailValueTv
        userPhone = binding.mobileValueTv

        companyUser = binding.companyValueTv
        departmentUser = binding.departmentValueTv
        hiredAt = binding.dateStartValueTv

        viewModel.initViewModel()
    }

    companion object {

        const val TAG = "ProfileFragment"

        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}
