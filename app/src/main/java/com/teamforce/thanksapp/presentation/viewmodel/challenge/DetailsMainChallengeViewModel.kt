package com.teamforce.thanksapp.presentation.viewmodel.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.response.GetChallengeResultResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.model.domain.ChallengeModelById
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsMainChallengeViewModel @Inject constructor(
   private val userDataRepository: UserDataRepository,
   private val challengeRepository: ChallengeRepository
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessOperationMyResult = MutableLiveData<Boolean>()
    val isSuccessOperationMyResult: LiveData<Boolean> = _isSuccessOperationMyResult
    private val _myResult = MutableLiveData<GetChallengeResultResponse?>()
    val myResult: LiveData<GetChallengeResultResponse?> = _myResult
    private val _myResultError = MutableLiveData<String>()
    val myResultError: LiveData<String> = _myResultError



    private val _challenge = MutableLiveData<ChallengeModelById?>()
    val challenge: LiveData<ChallengeModelById?> = _challenge
    private val _getChallengeError = MutableLiveData<String>()
    val getChallengeError: LiveData<String> = _getChallengeError


    fun loadChallenge(
        challengeId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                when (val result = challengeRepository.getChallengeById(challengeId)) {
                    is ResultWrapper.Success -> {
                        _challenge.postValue(result.value)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _getChallengeError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _getChallengeError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }


    fun loadChallengeResult(
        challengeId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                _isSuccessOperationMyResult.postValue(false)
                when (val result = challengeRepository.loadChallengeResult(challengeId)) {
                    is ResultWrapper.Success -> {
                        _isSuccessOperationMyResult.postValue(true)
                        _myResult.postValue(result.value[0])
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _isSuccessOperationMyResult.postValue(false)
                            _myResultError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _isSuccessOperationMyResult.postValue(false)
                            _myResultError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }


    fun getProfileId(): Int{
        val id = userDataRepository.getProfileId()
        if(!id.isNullOrEmpty()){
            return id.toInt()
        }
        return -1
    }
}