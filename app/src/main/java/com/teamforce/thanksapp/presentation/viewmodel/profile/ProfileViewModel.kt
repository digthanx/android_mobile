package com.teamforce.thanksapp.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.NotificationsRepository
import com.teamforce.thanksapp.PushNotificationService
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.response.ChangeOrgResponse
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import com.teamforce.thanksapp.domain.repositories.ProfileRepository
import com.teamforce.thanksapp.domain.usecases.LoadProfileUseCase
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

}
