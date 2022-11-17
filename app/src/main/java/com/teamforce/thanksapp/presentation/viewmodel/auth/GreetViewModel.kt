package com.teamforce.thanksapp.presentation.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GreetViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
): ViewModel() {
    fun getXcode() = userDataRepository.getXcode()
    fun getXId() = userDataRepository.getXid()
    fun getOrgId() = userDataRepository.getOrgCode()
}