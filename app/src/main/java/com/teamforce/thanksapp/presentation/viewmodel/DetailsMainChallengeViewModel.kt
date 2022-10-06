package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsMainChallengeViewModel @Inject constructor(
    userDataRepository: UserDataRepository
): ViewModel() {

    private val userRepo = userDataRepository

    fun getProfileId(): Int{
        val id = userRepo.getProfileId()
        if(!id.isNullOrEmpty()){
            return id.toInt()
        }
        return -1
    }
}