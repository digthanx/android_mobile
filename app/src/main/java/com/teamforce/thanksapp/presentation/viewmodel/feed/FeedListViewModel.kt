package com.teamforce.thanksapp.presentation.viewmodel.feed

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.presentation.fragment.feedScreen.FeedFragment
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val feedRepository: FeedRepository
) : ViewModel() {

    fun getUsername(): String {
        return userDataRepository.getUserName()!!
    }

    val all = feedRepository.getEvents().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope)

    val transactions = feedRepository.getTransactions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope)

    val winners = feedRepository.getWinners().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope)

    val challenges = feedRepository.getChallenges().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PagingData.empty()
    ).cachedIn(viewModelScope)
}