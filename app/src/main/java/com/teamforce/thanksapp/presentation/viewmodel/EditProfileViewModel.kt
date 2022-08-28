package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.UpdateProfileResponse
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileViewModel(): ViewModel(){


    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateProfile = MutableLiveData<UpdateProfileResponse>()
    val updateProfile: LiveData<UpdateProfileResponse> = _updateProfile
    private val _updateProfileError = MutableLiveData<String>()
    val updateProfileError: LiveData<String> = _updateProfileError



    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }

    fun loadUpdateProfile(token: String, userId: String, tgName: String, surname: String,
                          firstName: String, middleName: String, nickname: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUpdateProfileEndpoint(token, userId = userId, tgName, surname,
            firstName, middleName, nickname, Dispatchers.Default) }
    }

    private suspend fun callUpdateProfileEndpoint(
        token: String,
        userId: String,
        tgName: String, surname: String,
        firstName: String, middleName: String, nickname: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.updateProfile("Token $token", userId = userId, tgName, surname,
                firstName, middleName, nickname)?.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        Log.d("Token", "${response.body()}")
                        Log.d("Token", "Я внутри успешного вызова функции выше response body")
                        _updateProfile.postValue(response.body())
                    } else {
                        _updateProfileError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _updateProfileError.postValue(t.message)
                }
            })
        }
    }
}