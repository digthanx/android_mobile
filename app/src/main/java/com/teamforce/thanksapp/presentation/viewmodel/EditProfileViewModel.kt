package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CreateContactRequest
import com.teamforce.thanksapp.data.request.UpdateContactRequest
import com.teamforce.thanksapp.data.request.UpdateProfileRequest
import com.teamforce.thanksapp.data.response.CreateContactResponse
import com.teamforce.thanksapp.data.response.UpdateContactResponse
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


    private val _createContact = MutableLiveData<CreateContactResponse>()
    val createContact: LiveData<CreateContactResponse> = _createContact
    private val _createContactError = MutableLiveData<String>()
    val createContactError: LiveData<String> = _createContactError

    private val _updateContact = MutableLiveData<UpdateContactResponse>()
    val updateContact: LiveData<UpdateContactResponse> = _updateContact
    private val _updateContactError = MutableLiveData<String>()
    val updateContactError: LiveData<String> = _updateContactError

    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation



    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
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


    fun loadCreateContact(token: String, contactId: String, contactType: String, profile: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callCreateContactEndpoint(token, contactId, contactType, profile, Dispatchers.Default) }
    }

    private suspend fun callCreateContactEndpoint(
        token: String,
        contactId: String,
        contactType: String,
        profile: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.createContact("Token $token",
                CreateContactRequest(
                contactId,
                contactType,
                profile))?.enqueue(object : Callback<CreateContactResponse> {
                override fun onResponse(
                    call: Call<CreateContactResponse>,
                    response: Response<CreateContactResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _createContact.postValue(response.body())
                    } else {
                        _createContactError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<CreateContactResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _createContactError.postValue(t.message)
                }
            })
        }
    }


    fun loadUpdateContact(token: String, contactId: String, contactValue: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUpdateContactEndpoint(token, contactId, contactValue, Dispatchers.Default) }
    }

    private suspend fun callUpdateContactEndpoint(
        token: String,
        contactId: String,
        contactValue: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.updateContact("Token $token",contactId, UpdateContactRequest(contactValue))?.enqueue(object : Callback<UpdateContactResponse> {
                override fun onResponse(
                    call: Call<UpdateContactResponse>,
                    response: Response<UpdateContactResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _updateContact.postValue(response.body())
                    } else {
                        _updateContactError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<UpdateContactResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _updateContactError.postValue(t.message)
                }
            })
        }
    }
}