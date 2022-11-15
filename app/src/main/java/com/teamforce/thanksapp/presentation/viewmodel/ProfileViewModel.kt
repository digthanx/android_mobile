package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.NotificationsRepository
import com.teamforce.thanksapp.PushNotificationService
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.request.AuthorizationRequest
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import com.teamforce.thanksapp.domain.repositories.ProfileRepository
import com.teamforce.thanksapp.domain.usecases.LoadProfileUseCase
import com.teamforce.thanksapp.utils.Result
import com.teamforce.thanksapp.utils.ResultWrapper
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
class ProfileViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val loadProfileUseCase: LoadProfileUseCase,
    private val profileRepository: ProfileRepository,
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _profile = MutableLiveData<ProfileModel>()

    private val _isSuccessfulOperation = MutableLiveData<Boolean>()
    val isSuccessfulOperation: LiveData<Boolean> = _isSuccessfulOperation

    val profile: LiveData<ProfileModel> = _profile
    private val _profileError = MutableLiveData<String>()

    private val _organisations = MutableLiveData<List<OrganizationModel>?>()
    val organizations: LiveData<List<OrganizationModel>?> = _organisations
    private val _organisationsError = MutableLiveData<String>()
    val organizationsError: LiveData<String> = _organisationsError

    private val _authResult = MutableLiveData<Boolean>()
    val authResult: LiveData<Boolean> = _authResult

    private var xCode: String? = null
    private var xId: String? = null
    private var xEmail: String? = null
    var authorizationType: AuthorizationType? = null




    fun changeOrg(orgId: Int) {
        _isLoading.postValue(true)
        viewModelScope.launch { callChangeOrg(orgId, Dispatchers.Default) }
    }

    private suspend fun callChangeOrg(
        orgId: Int,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            profileRepository.changeOrganization(orgId)
                .enqueue(object : Callback<Any> {
                    override fun onResponse(
                        call: Call<Any>,
                        response: Response<Any>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Status запроса: ${response.body().toString()}")
                            if (response.body().toString() == "{status=Код отправлен в телеграм}") {
                                xId = response.headers().get("X-Telegram")
                            }
                            if (response.body()
                                    .toString() == "{status=Код отправлен на указанную электронную почту}"
                            ) {
                                xEmail = response.headers().get("X-Email")
                                authorizationType = AuthorizationType.Email
                            }
                            xCode = response.headers().get("X-Code")
                            _authResult.postValue(true)
                        } else {
                            _authResult.postValue(false)
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        _isLoading.postValue(false)
                        _authResult.postValue(false)
                    }
                })
        }
    }

//    fun changeOrg(orgId: Int) {
//        _isLoading.postValue(true)
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                _isLoading.postValue(true)
//
//                when (val result = profileRepository.changeOrganization(orgId)) {
//                    is ResultWrapper.Success -> {
//                        xId = result.headers().get("X-Telegram")
//                        xCode =
//                    }
//                    is ResultWrapper.GenericError ->
//                        _organisationsError.postValue(result.error + " " + result.code)
//
//                    is ResultWrapper.NetworkError ->
//                        _organisationsError.postValue("Ошибка сети")
//                }
//                _isLoading.postValue(false)
//            }
//        }
//    }

    fun loadUserOrganizations() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)

                when (val result = profileRepository.getOrganizations()) {
                    is ResultWrapper.Success -> {
                        _organisations.postValue(result.value)
                    }
                    is ResultWrapper.GenericError ->
                        _organisationsError.postValue(result.error + " " + result.code)

                    is ResultWrapper.NetworkError ->
                        _organisationsError.postValue("Ошибка сети")
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun loadUserProfile() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.postValue(true)

                when (val result = loadProfileUseCase()) {
                    is ResultWrapper.Success -> {
                        _profile.postValue(result.value!!)
                        Log.d("Token", "Сохраняем id профиля ${result.value.profile.id}")
                        userDataRepository.saveProfileId(result.value.profile.id)
                        userDataRepository.saveUsername(result.value.profile.tgName)
                    }
                    is ResultWrapper.GenericError ->
                        _profileError.postValue(result.error + " " + result.code)

                    is ResultWrapper.NetworkError ->
                        _profileError.postValue("Ошибка сети")
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun loadUpdateAvatarUserProfile(
        filePath: String,
        filePathCropped: String
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val userId = userDataRepository.getProfileId()
            if (userId != null)
                withContext(Dispatchers.IO) {
                    when (val result = profileRepository.updateUserAvatar(userId, filePath, filePathCropped)) {
                        is ResultWrapper.Success -> {
                            _isSuccessfulOperation.postValue(true)
                        }
                        else -> {
                            if (result is ResultWrapper.GenericError) {
                                _profileError.postValue(result.error + " " + result.code)

                            } else if (result is ResultWrapper.NetworkError) {
                                _profileError.postValue("Ошибка сети")
                            }
                        }
                    }
                    _isLoading.postValue(false)
                }
        }
    }

    fun logout(deviceId: String) {
        //при выходе из аккаунта удаляем пуш токен, чтобы не приходили уведомления
        viewModelScope.launch {
            try {
                notificationsRepository.deletePushToken(
                    deviceId
                )
            } catch (e:Exception) {
                Log.d(PushNotificationService.TAG, "logout: error $e")
            }
            userDataRepository.logout()
        }
    }



    fun isUserAuthorized() = userDataRepository.getAuthToken() != null


    sealed class AuthorizationType {
        object Email : AuthorizationType()
        object Telegram : AuthorizationType()
    }

}
