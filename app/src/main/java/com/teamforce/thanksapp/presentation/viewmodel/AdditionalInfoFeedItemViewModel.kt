package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CreateCommentRequest
import com.teamforce.thanksapp.data.request.GetCommentsRequest
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.GetCommentsResponse
import com.teamforce.thanksapp.domain.models.feed.FeedItemByIdModel
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdditionalInfoFeedItemViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository,
    val feedRepository: FeedRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dataOfTransaction = MutableLiveData<FeedItemByIdModel?>()
    val dataOfTransaction: MutableLiveData<FeedItemByIdModel?> = _dataOfTransaction

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error


    private val _pressLikesError = MutableLiveData<String>()
    val pressLikesError: LiveData<String> = _pressLikesError
    private val _isLoadingLikes = MutableLiveData<Boolean>()
    val isLoadingLikes: LiveData<Boolean> = _isLoadingLikes

    fun getProfileUserName() = userDataRepository.getUserName()


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



    fun pressLike(mapReactions: Map<String, Int>) {
        _isLoadingLikes.postValue(true)
        viewModelScope.launch { callPressLikeEndpoint(mapReactions, Dispatchers.IO) }


    }

    private suspend fun callPressLikeEndpoint(
        listReactions: Map<String, Int>,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi.pressLike(listReactions)
                .enqueue(object : Callback<CancelTransactionResponse> {
                    override fun onResponse(
                        call: Call<CancelTransactionResponse>,
                        response: Response<CancelTransactionResponse>
                    ) {
                        _isLoadingLikes.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Успешно лайк отправил")
                        } else {
                            _pressLikesError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<CancelTransactionResponse>, t: Throwable) {
                        _isLoadingLikes.postValue(false)
                        _pressLikesError.postValue(t.message)
                    }
                })
        }
    }
}