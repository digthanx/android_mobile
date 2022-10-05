package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.profile.ContactEntity
import com.teamforce.thanksapp.data.network.models.Contact
import com.teamforce.thanksapp.data.request.UpdateProfileRequest
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.UpdateFewContactsResponse
import com.teamforce.thanksapp.data.response.UpdateProfileResponse
import com.teamforce.thanksapp.domain.models.profile.ContactModel
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import com.teamforce.thanksapp.domain.usecases.LoadProfileUseCase
import com.teamforce.thanksapp.utils.ResultWrapper
import com.teamforce.thanksapp.utils.RetrofitClient
import com.teamforce.thanksapp.utils.UserDataRepository
import com.teamforce.thanksapp.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository,
    private val loadProfileUseCase: LoadProfileUseCase
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateProfile = MutableLiveData<UpdateProfileResponse>()
    val updateProfile: LiveData<UpdateProfileResponse> = _updateProfile
    private val _updateProfileError = MutableLiveData<String>()
    val updateProfileError: LiveData<String> = _updateProfileError


    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation


    private val _profile = MutableLiveData<ProfileModel>()
    val profile: LiveData<ProfileModel> = _profile
    private val _profileError = MutableLiveData<String>()
    val profileError: LiveData<String> = _profileError


    private val _updateFewContact = MutableLiveData<UpdateFewContactsResponse>()
    val updateFewContact: LiveData<UpdateFewContactsResponse> = _updateFewContact
    private val _updateFewContactError = MutableLiveData<String>()
    val updateFewContactError: LiveData<String> = _updateFewContactError

    fun loadUserProfile() {
        _isLoading.postValue(true)
        viewModelScope.launch { callProfileEndpoint(Dispatchers.Default) }
    }

    private suspend fun callProfileEndpoint(
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {

            when (val result = loadProfileUseCase()) {
                is ResultWrapper.Success -> {
                    _profile.postValue(result.value!!)
                }
                else -> {
                    if (result is ResultWrapper.GenericError) {
                        _profileError.postValue(result.error + " " + result.code)

                    } else if (result is ResultWrapper.NetworkError) {
                        _profileError.postValue("Ошибка сети")
                    }
                }
            }
        }
    }


    fun loadUpdateProfile(
        tgName: String?, surname: String?,
        firstName: String?, middleName: String?, nickname: String?
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            if (userDataRepository.getProfileId() != null)
                callUpdateProfileEndpoint(
                    userDataRepository.getProfileId()!!, tgName, surname,
                    firstName, middleName, nickname, Dispatchers.Default
                )
        }
    }

    private suspend fun callUpdateProfileEndpoint(
        userId: String,
        tgName: String?, surname: String?,
        firstName: String?, middleName: String?, nickname: String?,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.updateProfile(
                userId = userId, UpdateProfileRequest(
                    tgName, surname,
                    firstName, middleName, nickname
                )
            ).enqueue(object : Callback<UpdateProfileResponse> {
                override fun onResponse(
                    call: Call<UpdateProfileResponse>,
                    response: Response<UpdateProfileResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        Log.d("Token", "${response.body()}")
                        _updateProfile.postValue(response.body())
                    } else {
                        val jArrayError = JSONArray(response.errorBody()!!.string())
                        _updateProfileError.postValue(
                            jArrayError.toString()
                                .subSequence(2, jArrayError.toString().length - 2).toString()
                        )
                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _updateProfileError.postValue(t.message)
                }
            })
        }
    }


    fun loadUpdateFewContact(listContacts: List<ContactModel>) {
        val contacts = listContacts.map {
            ContactEntity(
                id = it.id,
                contact_id = it.contactId,
                contact_type = it.contactType
            )
        }
        _isLoading.postValue(true)
        viewModelScope.launch {
            callUpdateFewContactEndpoint(
                contacts,
                Dispatchers.Default
            )
        }
    }

    private suspend fun callUpdateFewContactEndpoint(
        listContacts: List<ContactEntity>,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.updateFewContact(listContacts)
                .enqueue(object : Callback<UpdateFewContactsResponse> {
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