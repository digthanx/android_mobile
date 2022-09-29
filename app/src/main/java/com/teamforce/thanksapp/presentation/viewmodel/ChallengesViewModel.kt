package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.CreateChallengeResponse
import com.teamforce.thanksapp.data.response.GetChallengesResponse
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

class ChallengesViewModel: ViewModel() {

    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getChallenges = MutableLiveData<GetChallengesResponse>()
    val getChallenges: LiveData<GetChallengesResponse> = _getChallenges
    private val _getChallengesError = MutableLiveData<String>()
    val getChallengesError: LiveData<String> = _getChallengesError

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }


    fun loadChallenges(){
        _isLoading.postValue(true)
        UserDataRepository.getInstance()?.token?.let {
            viewModelScope.launch { callGetChallengesEndpoint(
                it, Dispatchers.Default)
            }
        }

    }

    private suspend fun callGetChallengesEndpoint(
        token: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {

            thanksApi?.getChallenges("Token $token")
                ?.enqueue(object : Callback<GetChallengesResponse> {
                override fun onResponse(
                    call: Call<GetChallengesResponse>,
                    response: Response<GetChallengesResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _getChallenges.postValue(response.body())
                    } else {
                        _getChallengesError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<GetChallengesResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _getChallengesError.postValue(t.message)
                }
            })
        }
    }

}