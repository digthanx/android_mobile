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
import com.teamforce.thanksapp.utils.RetrofitClient
import com.teamforce.thanksapp.utils.UserDataRepository
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

class CreateChallengeViewModel: ViewModel() {

    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _createChallenge = MutableLiveData<CreateChallengeResponse>()
    val createChallenge: LiveData<CreateChallengeResponse> = _createChallenge
    private val _createChallengeError = MutableLiveData<String>()
    val createChallengeError: LiveData<String> = _createChallengeError

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }


    fun createChallenge(name: String, description: String,
                        endAt: String, amountFund: Int, photo: MultipartBody.Part?, parameters: List<Map<String, Int>>) {
        _isLoading.postValue(true)
        UserDataRepository.getInstance()?.token?.let {
            viewModelScope.launch { callCreateChallengeEndpoint(
                it, name, description, endAt, amountFund, photo, parameters, Dispatchers.Default)
            }
        }

    }

    private suspend fun callCreateChallengeEndpoint(
        token: String,
        name: String,
        description: String,
        endAt: String,
        amountFund: Int,
        photo: MultipartBody.Part?,
        parameters: List<Map<String, Int>>,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            val nameB = RequestBody.create(MediaType.parse("multipart/form-data"), name)
            val descriptionB = RequestBody.create(MediaType.parse("multipart/form-data"), description)
            val endAtB =
                RequestBody.create(MediaType.parse("multipart/form-data"), endAt)
            val amountFundB =
                RequestBody.create(MediaType.parse("multipart/form-data"), amountFund.toString())
            val parametersJson = Gson().toJson(parameters)
            val parametersB =
                RequestBody.create(MediaType.parse("application/json"), parametersJson)



            thanksApi?.createChallenge(
                "Token $token",
                photo,
                nameB,
                descriptionB,
                endAtB,
                amountFundB,
                parametersB
            )?.enqueue(object : Callback<CreateChallengeResponse> {
                override fun onResponse(
                    call: Call<CreateChallengeResponse>,
                    response: Response<CreateChallengeResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _createChallenge.postValue(response.body())
                        Log.d("Token", "Пользовательские данные ${response.body()}")
                    } else {
                        _createChallengeError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<CreateChallengeResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _createChallengeError.postValue(t.message)
                }
            })
        }
    }
}