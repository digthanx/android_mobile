package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.data.request.CreateContactRequest
import com.teamforce.thanksapp.data.request.UpdateContactRequest
import com.teamforce.thanksapp.data.request.UpdateProfileRequest
import com.teamforce.thanksapp.data.response.*
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


    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation


    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile
    private val _profileError = MutableLiveData<String>()
    val profileError: LiveData<String> = _profileError


    private val _updateFewContact = MutableLiveData<UpdateFewContactsResponse>()
    val updateFewContact: LiveData<UpdateFewContactsResponse> = _updateFewContact
    private val _updateFewContactError = MutableLiveData<String>()
    val updateFewContactError: LiveData<String> = _updateFewContactError


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



    fun loadUpdateProfile(token: String, userId: String, tgName: String?, surname: String?,
                          firstName: String?, middleName: String?, nickname: String?) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUpdateProfileEndpoint(token, userId = userId, tgName, surname,
            firstName, middleName, nickname, Dispatchers.Default) }
    }

    private suspend fun callUpdateProfileEndpoint(
        token: String,
        userId: String,
        tgName: String?, surname: String?,
        firstName: String?, middleName: String?, nickname: String?,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.updateProfile("Token $token", userId = userId, UpdateProfileRequest(tgName, surname,
                firstName, middleName, nickname))?.enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        Log.d("Token", "${response.body()}")
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



    fun loadUpdateFewContact(token: String, listContacts: List<Contact>) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUpdateFewContactEndpoint(token, listContacts, Dispatchers.Default) }
    }

    private suspend fun callUpdateFewContactEndpoint(
        token: String,
        listContacts: List<Contact>,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.updateFewContact("Token $token", listContacts)?.enqueue(object : Callback<UpdateFewContactsResponse> {
                override fun onResponse(
                    call: Call<UpdateFewContactsResponse>,
                    response: Response<UpdateFewContactsResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _updateFewContact.postValue(response.body())
                    } else {
                        _updateFewContactError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<UpdateFewContactsResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _updateFewContactError.postValue(t.message)
                }
            })
        }
    }


}