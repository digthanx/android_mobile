package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.request.CreateReportRequest
import com.teamforce.thanksapp.data.response.CreateReportResponse
import com.teamforce.thanksapp.data.response.GetChallengeContendersResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation

    private val _createReport = MutableLiveData<CreateReportResponse>()
    val contenders: LiveData<CreateReportResponse> = _createReport
    private val _createReportError = MutableLiveData<String>()
    val createReportError: LiveData<String> = _createReportError


    fun createReport(
        challengeId: Int,
        comment: String,
        image: MultipartBody.Part?
    ) {
        val challengeIdRequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), challengeId.toString())
        val commentRequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), comment)
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)
                _isSuccessOperation.postValue(false)
                when (val result = challengeRepository.createReport(
                    image,
                    challengeIdRequestBody,
                    commentRequestBody)) {
                    is ResultWrapper.Success -> {
                        _isSuccessOperation.postValue(true)
                        _createReport.postValue(result.value!!)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _createReportError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _createReportError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }
}