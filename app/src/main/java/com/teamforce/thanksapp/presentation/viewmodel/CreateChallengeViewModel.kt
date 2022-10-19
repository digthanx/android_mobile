package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.BalanceResponse
import com.teamforce.thanksapp.data.response.CreateChallengeResponse
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.utils.RetrofitClient
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CreateChallengeViewModel @Inject constructor(
    private val thanksApi: ThanksApi
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _createChallenge = MutableLiveData<ChallengeModel>()
    val createChallenge: LiveData<ChallengeModel> = _createChallenge
    private val _createChallengeError = MutableLiveData<String>()
    val createChallengeError: LiveData<String> = _createChallengeError
    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation


    fun createChallenge(
        name: String,
        description: String,
        endAt: String?,
        amountFund: Int,
        photo: MultipartBody.Part?,
        parameter_id: Int,
        parameter_value: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callCreateChallengeEndpoint(
                name, description, endAt, amountFund, photo, parameter_id, parameter_value, Dispatchers.Default
            )
        }

    }

    private suspend fun callCreateChallengeEndpoint(
        name: String,
        description: String,
        endAt: String?,
        amountFund: Int,
        photo: MultipartBody.Part?,
        parameter_id: Int,
        parameter_value: Int,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            val nameB = RequestBody.create(MediaType.parse("multipart/form-data"), name)
            val descriptionB =
                RequestBody.create(MediaType.parse("multipart/form-data"), description)
            val endAtB =
                endAt?.let { RequestBody.create(MediaType.parse("multipart/form-data"), it) }
            val amountFundB =
                RequestBody.create(MediaType.parse("multipart/form-data"), amountFund.toString())
            val parameter_idB =
                RequestBody.create(MediaType.parse("multipart/form-data"), parameter_id.toString())
            val parameter_valueB =
                RequestBody.create(MediaType.parse("multipart/form-data"), parameter_value.toString())


            thanksApi.createChallenge(
                photo,
                nameB,
                descriptionB,
                endAtB,
                amountFundB,
                parameter_idB,
                parameter_valueB
            ).enqueue(object : Callback<ChallengeModel> {
                override fun onResponse(
                    call: Call<ChallengeModel>,
                    response: Response<ChallengeModel>
                ) {
                    _isSuccessOperation.postValue(false)
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _isSuccessOperation.postValue(true)
                        _createChallenge.postValue(response.body())
                        Log.d("Token", "Пользовательские данные ${response.body()}")
                    } else {
                        _createChallengeError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<ChallengeModel>, t: Throwable) {
                    _isLoading.postValue(false)
                    _createChallengeError.postValue(t.message)
                }
            })
        }
    }
}