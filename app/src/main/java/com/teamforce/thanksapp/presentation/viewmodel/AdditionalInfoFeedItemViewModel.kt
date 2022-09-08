package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AdditionalInfoFeedItemViewModel @Inject constructor(
    val userDataRepository: UserDataRepository

) : ViewModel() {
}