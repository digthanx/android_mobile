package com.teamforce.thanksapp.presentation.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.domain.models.feed.FeedItemByIdModel
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailInnerFeedViewModel @Inject constructor(
    val feedRepository: FeedRepository
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dataOfTransaction = MutableLiveData<FeedItemByIdModel?>()
    val dataOfTransaction: MutableLiveData<FeedItemByIdModel?> = _dataOfTransaction

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    fun loadTransactionDetail(
        transactionId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                when (val result = feedRepository.getTransactionById(transactionId)) {
                    is ResultWrapper.Success -> {
                        _dataOfTransaction.postValue(result.value)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _error.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _error.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }
}