package com.teamforce.thanksapp.presentation.viewmodel.challenge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.model.domain.ChallengeModelById
import com.teamforce.thanksapp.utils.ResultWrapper
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
class DetailsInnerChallengerViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _challenge = MutableLiveData<ChallengeModelById>()
    val challenge: LiveData<ChallengeModelById> = _challenge
    private val _getChallengeError = MutableLiveData<String>()
    val getChallengeError: LiveData<String> = _getChallengeError

    private val _likeResult =  MutableLiveData<CancelTransactionResponse?>()
    val likeResult: LiveData<CancelTransactionResponse?> =  _likeResult
    private val _likeError = MutableLiveData<String>()
    val likeError: LiveData<String> = _likeError


    fun pressLike(
        challengeId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                when (val result = challengeRepository.pressLike(challengeId)) {
                    is ResultWrapper.Success -> {
                        _likeResult.postValue(result.value)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _likeError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _likeError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }





    fun loadChallenge(challengeId: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callGetChallengeEndpoint(
                challengeId, Dispatchers.IO
            )
        }

    }

    private suspend fun callGetChallengeEndpoint(
        challengeId: Int,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {

            thanksApi.getChallenge(challengeId)
                .enqueue(object : Callback<ChallengeModelById> {
                    override fun onResponse(
                        call: Call<ChallengeModelById>,
                        response: Response<ChallengeModelById>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200 || response.code() == 201) {
                            _challenge.postValue(response.body())
                            Log.d("Token", "Challenges in request ${response.body()}")
                        } else {
                            _getChallengeError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<ChallengeModelById>, t: Throwable) {
                        _isLoading.postValue(false)
                        _getChallengeError.postValue(t.message)
                    }
                })
        }
    }
}