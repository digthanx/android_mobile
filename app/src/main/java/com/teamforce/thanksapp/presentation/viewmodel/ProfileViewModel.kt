package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.PutUserAvatarRequest
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.data.response.UpdateProfileResponse
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileViewModel() : ViewModel() {

    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile
    private val _profileError = MutableLiveData<String>()
    val profileError: LiveData<String> = _profileError

    private val _imageUri = MutableLiveData<PutUserAvatarResponse>()
    val imageUri: LiveData<PutUserAvatarResponse> = _imageUri
    private val _imageUriError = MutableLiveData<String>()
    val imageUriError: LiveData<String> = _imageUriError

    private val _updateProfile = MutableLiveData<UpdateProfileResponse>()
    val updateProfile: LiveData<UpdateProfileResponse> = _updateProfile
    private val _updateProfileError = MutableLiveData<String>()
    val updateProfileError: LiveData<String> = _updateProfileError


    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }

    fun loadUserProfile(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callProfileEndpoint(token, Dispatchers.Default) }
    }

    private suspend fun callProfileEndpoint(
        token: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.getProfile("Token $token")?.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _profile.postValue(response.body())
                    } else {
                        _profileError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _profileError.postValue(t.message)
                }
            })
        }
    }


    fun loadUpdateAvatarUserProfile(token: String, userId: String, imageFilePart: MultipartBody.Part) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUpdateAvatarProfileEndpoint(token, userId = userId, imageFilePart, Dispatchers.Default) }
    }

    private suspend fun callUpdateAvatarProfileEndpoint(
        token: String,
        userId: String,
        imageFilePart: MultipartBody.Part,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            Log.d("Token", "Я внутри вызова функции Вот image ${imageFilePart}")
            thanksApi?.putUserAvatar("Token $token", userId = userId, imageFilePart)?.enqueue(object : Callback<PutUserAvatarResponse> {
                override fun onResponse(
                    call: Call<PutUserAvatarResponse>,
                    response: Response<PutUserAvatarResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _imageUri.postValue(response.body())
                    } else {
                        _imageUriError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<PutUserAvatarResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _profileError.postValue(t.message)
                }
            })
        }
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
