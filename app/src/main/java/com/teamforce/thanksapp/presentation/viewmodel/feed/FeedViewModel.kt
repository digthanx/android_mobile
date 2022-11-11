package com.teamforce.thanksapp.presentation.viewmodel.feed

import androidx.lifecycle.ViewModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    val userDataRepository: UserDataRepository,
) : ViewModel() {}