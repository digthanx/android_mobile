package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.ProfileResponse
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
class SomeonesProfileViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _anotherProfile = MutableLiveData<ProfileResponse>()
    val anotherProfile: LiveData<ProfileResponse> = _anotherProfile
    private val _anotherProfileError = MutableLiveData<String>()
    val profileError: LiveData<String> = _anotherProfileError

    fun loadAnotherUserProfile(userId: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch { callAnotherProfileEndpoint(userId, Dispatchers.Default) }
    }

    private suspend fun callAnotherProfileEndpoint(
        userId: Int,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.getAnotherProfile(user_Id = userId)
                .enqueue(object : Callback<ProfileResponse> {
                    override fun onResponse(
                        call: Call<ProfileResponse>,
                        response: Response<ProfileResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            _anotherProfile.postValue(response.body())
                        } else {
                            _anotherProfileError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _anotherProfileError.postValue(t.message)
                    }
                })
        }
    }

}