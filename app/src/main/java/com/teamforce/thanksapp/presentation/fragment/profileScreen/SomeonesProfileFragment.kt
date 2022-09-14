package com.teamforce.thanksapp.presentation.fragment.profileScreen

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.teamforce.thanksapp.R
import com.teamforce.thanksapp.databinding.FragmentProfileBinding
import com.teamforce.thanksapp.databinding.FragmentSomeonesProfileBinding
import com.teamforce.thanksapp.presentation.viewmodel.ProfileViewModel
import com.teamforce.thanksapp.presentation.viewmodel.SomeonesProfileViewModel
import com.teamforce.thanksapp.utils.Consts
import com.teamforce.thanksapp.utils.UserDataRepository


class SomeonesProfileFragment : Fragment(R.layout.fragment_someones_profile) {

    // reflection API and ViewBinding.bind are used under the hood
    private val binding: FragmentSomeonesProfileBinding by viewBinding()

    private val viewModel = SomeonesProfileViewModel()


    private val userAvatar: ImageView by lazy { binding.userAvatar }
    private val userName: TextView by lazy { binding.firstNameValueTv }
    private val userSurname: TextView by lazy { binding.surnameValueTv }
    private val userMiddleName: TextView by lazy { binding.middleNameValueTv }
    private val userEmail: TextView by lazy { binding.emailValueTv }
    private val userPhone: TextView by lazy { binding.mobileValueTv }
    private val greetingUser: TextView by lazy { binding.greetingUserTv }
    private val companyUser: TextView by lazy { binding.companyValueTv }
    private val positionUser: TextView by lazy { binding.positionValueTv }
    private val closeBtn: MaterialButton by lazy { binding.closeBtn }
    private val progressBar: ProgressBar by lazy { binding.progressBar }

    private var userId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("userId")
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initViewModel()

        requestData()
        setData()
    }

    private fun initViews(){
        viewModel.isLoading.observe(
            viewLifecycleOwner,
            Observer { isLoading ->
                if (isLoading) {
                    binding.header.visibility = View.GONE
                    binding.information.visibility = View.GONE
                    binding.placeJob.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                } else {
                    binding.header.visibility = View.VISIBLE
                    binding.information.visibility = View.VISIBLE
                    binding.placeJob.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        )
    }

    private fun requestData(){
        val token = UserDataRepository.getInstance()?.token
        if (token != null) {
            userId?.let {
                viewModel.loadAnotherUserProfile(token, it)
            }
        }
    }
    private fun setData(){
        viewModel.anotherProfile.observe(viewLifecycleOwner){
            userName.text = it.profile.firstname
            userSurname.text = it.profile.surname
            userMiddleName.text = it.profile.middlename
            companyUser.text = it.profile.organization
            positionUser.text = it.profile.jobTitle
            UserDataRepository.getInstance()?.username?.let {
                greetingUser(it)
            }
            if(it.profile.contacts.size == 1){
                if(it.profile.contacts[0].contact_type == "@"){
                    userEmail.text = it.profile.contacts[0].contact_id
                }else{
                    userPhone.text = it.profile.contacts[0].contact_id
                }
            }else if(it.profile.contacts.size == 2){
                if(it.profile.contacts[0].contact_type == "@"){
                    userEmail.text = it.profile.contacts[0].contact_id
                    userPhone.text = it.profile.contacts[1].contact_id
                }else{
                    userEmail.text = it.profile.contacts[1].contact_id
                    userPhone.text = it.profile.contacts[0].contact_id
                }
            }

            if(!it.profile.photo.isNullOrEmpty()){
                Glide.with(this)
                    .load("${it.profile.photo}".toUri())
                    .centerCrop()
                    .into(userAvatar)
            }else {
                userAvatar.setImageResource(R.drawable.ic_anon_avatar)
            }


        }
    }

    private fun greetingUser(username: String){
        val spannable = SpannableStringBuilder(
            String.format(requireContext().getString(R.string.greeting_label), username)
        )
        spannable.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.general_brand)),
            7, spannable.length - 1,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        greetingUser.text = spannable
    }
}