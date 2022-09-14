package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.PutUserAvatarResponse
import com.teamforce.thanksapp.presentation.fragment.profileScreen.ProfileFragment
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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


    fun loadUpdateAvatarUserProfile(
        token: String,
        userId: String,
        imageFilePart: MultipartBody.Part
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callUpdateAvatarProfileEndpoint(
                token,
                userId = userId,
                imageFilePart,
                Dispatchers.Default
            )
        }
    }

    private suspend fun callUpdateAvatarProfileEndpoint(
        token: String,
        userId: String,
        imageFilePart: MultipartBody.Part,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.putUserAvatar("Token $token", userId = userId, imageFilePart)
                ?.enqueue(object : Callback<PutUserAvatarResponse> {
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
                        Log.d(ProfileFragment.TAG, "onFailure: $t")
                        _isLoading.postValue(false)
                        _profileError.postValue(t.message)
                    }
                })
        }
    }


}
