package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsMainChallengeViewModel @Inject constructor(
   private val userDataRepository: UserDataRepository
): ViewModel() {


    fun getProfileId(): Int{
        val id = userDataRepository.getProfileId()
        if(!id.isNullOrEmpty()){
            return id.toInt()
        }
        return -1
    }
}