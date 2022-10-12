package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContendersChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessOperation = MutableLiveData<SuccessResultCheckReport>()
    val isSuccessOperation: LiveData<SuccessResultCheckReport> = _isSuccessOperation

    private val _contenders = MutableLiveData<List<GetChallengeContendersResponse.Contender>>()
    val contenders: LiveData<List<GetChallengeContendersResponse.Contender>?> = _contenders
    private val _contendersError = MutableLiveData<String>()
    val contendersError: LiveData<String> = _contendersError

    private val _checkReportError = MutableLiveData<String>()
    val checkReportError: LiveData<String> = _checkReportError


    fun loadContenders(
        challengeId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                when (val result = challengeRepository.loadContenders(challengeId)) {
                    is ResultWrapper.Success -> {
                        _contenders.postValue(result.value!!)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _contendersError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _contendersError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun checkReport(
        reportId: Int,
        state: Char,
        reasonOfReject: String?
    ) {
        val stateMap = mapOf<String, Char>("state" to state)
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                _isSuccessOperation.postValue(SuccessResultCheckReport(state, false))
                when (val result = challengeRepository.checkChallengeReport(reportId, stateMap, reasonOfReject)) {
                    is ResultWrapper.Success -> {
                        _isSuccessOperation.postValue(SuccessResultCheckReport(state, true))
                        // _contenders.postValue(result.value!!)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _checkReportError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _checkReportError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }

     data class SuccessResultCheckReport(
         val state: Char,
         val successResult: Boolean
     )
}