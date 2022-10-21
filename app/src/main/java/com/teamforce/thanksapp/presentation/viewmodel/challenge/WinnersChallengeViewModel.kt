package com.teamforce.thanksapp.presentation.viewmodel.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.response.GetChallengeWinnersResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class WinnersChallengeViewModel @Inject constructor(
   private val challengeRepository: ChallengeRepository
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessOperation = MutableLiveData<ContendersChallengeViewModel.SuccessResultCheckReport>()
    val isSuccessOperation: LiveData<ContendersChallengeViewModel.SuccessResultCheckReport> = _isSuccessOperation

    private val _winners = MutableLiveData<List<GetChallengeWinnersResponse.Winner>?>()
    val winners: LiveData<List<GetChallengeWinnersResponse.Winner>?> = _winners
    private val _winnersError = MutableLiveData<String>()
    val winnersError: LiveData<String> = _winnersError




    fun loadWinners(
        challengeId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                when (val result = challengeRepository.loadWinners(challengeId)) {
                    is ResultWrapper.Success -> {
                        _winners.postValue(result.value)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _winnersError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _winnersError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }
}