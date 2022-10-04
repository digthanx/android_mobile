package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.GetChallengesResponse
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.utils.RetrofitClient
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
class ChallengesViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _challenges = MutableLiveData<List<ChallengeModel>>()
    val challenges: LiveData<List<ChallengeModel>> = _challenges
    private val _getChallengesError = MutableLiveData<String>()
    val getChallengesError: LiveData<String> = _getChallengesError


    fun loadChallenges(){
        _isLoading.postValue(true)
            viewModelScope.launch { callGetChallengesEndpoint(
                Dispatchers.IO)
            }

    }

    private suspend fun callGetChallengesEndpoint(
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {

            thanksApi.getChallenges()
                .enqueue(object : Callback<List<ChallengeModel>> {
                    override fun onResponse(
                        call: Call<List<ChallengeModel>>,
                        response: Response<List<ChallengeModel>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200 || response.code() == 201) {
                            _challenges.postValue(response.body())
                            Log.d("Token", "Challenges in request ${response.body()}")
                        } else {
                            _getChallengesError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<List<ChallengeModel>>, t: Throwable) {
                        _isLoading.postValue(false)
                        _getChallengesError.postValue(t.message)
                    }
                })
        }
    }

}