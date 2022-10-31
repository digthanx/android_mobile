package com.teamforce.thanksapp.presentation.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _pressLikesError = MutableLiveData<String>()
    val pressLikesError: LiveData<String> = _pressLikesError

    private val _pressLikes = MutableLiveData<String>()
    val pressLikes: LiveData<String> = _pressLikes




    fun pressLike(
        transactionId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                when (val result = feedRepository.pressLike(transactionId)) {
                    is ResultWrapper.Success -> {
                        _pressLikes.postValue(result.value.data)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _pressLikesError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _pressLikesError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }
}