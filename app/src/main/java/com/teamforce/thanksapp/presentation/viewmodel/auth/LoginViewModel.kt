package com.teamforce.thanksapp.presentation.viewmodel

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.*
import com.teamforce.thanksapp.NotificationsRepository
import com.teamforce.thanksapp.data.SharedPreferences
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.entities.notifications.PushTokenEntity
import com.teamforce.thanksapp.data.request.AuthorizationRequest
import com.teamforce.thanksapp.data.request.ChooseOrgRequest
import com.teamforce.thanksapp.data.request.VerificationRequest
import com.teamforce.thanksapp.data.response.AuthResponse
import com.teamforce.thanksapp.data.response.VerificationResponse
import com.teamforce.thanksapp.domain.models.profile.ProfileModel
import com.teamforce.thanksapp.domain.usecases.LoadProfileUseCase
import com.teamforce.thanksapp.model.domain.UserData
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
class LoginViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository,
    private val loadProfileUseCase: LoadProfileUseCase,
    private val sharedPreferences: SharedPreferences,
    private val notificationsRepository: NotificationsRepository,
    private val app: Application
) : AndroidViewModel(app) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var xId: String? = null
    private var xEmail: String? = null
    private var xCode: String? = null
    private var token: String? = null
    private var telegramOrEmail: String? = null

    var authorizationType: AuthorizationType? = null

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> = _authResult

    private val _chooseOrgResult = MutableLiveData<VerificationResponse>()
    val chooseOrgResult: LiveData<VerificationResponse> = _chooseOrgResult

    private val _organizations = MutableLiveData<AuthResponse>()
    val organizations: LiveData<AuthResponse> = _organizations

    private val _verifyResult = MutableLiveData<Result<UserData>>()
    val verifyResult: LiveData<Result<UserData>> = _verifyResult

    fun logout() {
        userDataRepository.logout()
        xId = null
        xEmail = null
        xCode = null
        token = null
        telegramOrEmail = null
    }

    private val _profile = MutableLiveData<ProfileModel>()
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


    fun authorizeUser(telegramIdOrEmail: String) {
        telegramOrEmail = telegramIdOrEmail
        _isLoading.postValue(true)
        viewModelScope.launch { callAuthorizationEndpoint(telegramIdOrEmail, Dispatchers.Default) }
    }

    private suspend fun callAuthorizationEndpoint(
        telegramId: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.authorization(AuthorizationRequest(login = telegramId))
                .enqueue(object : Callback<AuthResponse> {
                    override fun onResponse(
                        call: Call<AuthResponse>,
                        response: Response<AuthResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            _organizations.postValue(response.body())
                            Log.d("Token", "Status запроса: ${response.body().toString()}")
                            if (response.body()?.status.toString() == "{status=Код отправлен в телеграм}") {
                                xId = response.headers().get("X-Telegram")
                                authorizationType = AuthorizationType.Telegram
                            }
                            if (response.body()?.status
                                    .toString() == "{status=Код отправлен на указанную электронную почту}"
                            ) {
                                xEmail = response.headers().get("X-Email")
                                authorizationType = AuthorizationType.Email

                            }
                            xCode = response.headers().get("X-Code")
                            _authResult.postValue(Result.Success(true))
                        } else {
                            _authResult.postValue(Result.Error("${response.message()} ${response.code()}"))
                        }
                    }

                    override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _authResult.postValue(Result.Error(t.message ?: "Something went wrong"))
                    }
                })
        }
    }

    fun chooseOrg(userId: Int, orgId: Int, login: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callChooseOrgEndpoint(userId, orgId, login, Dispatchers.Default) }
    }

    private suspend fun callChooseOrgEndpoint(
        userId: Int,
        orgId: Int,
        login: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.chooseOrganization(login, ChooseOrgRequest(userId, orgId))
                .enqueue(object : Callback<VerificationResponse> {
                    override fun onResponse(
                        call: Call<VerificationResponse>,
                        response: Response<VerificationResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            _chooseOrgResult.postValue(response.body())
                            Log.d("Token", "Status запроса: ${response.body().toString()}")
                            xId = response.headers().get("X-Telegram")
                            xEmail = response.headers().get("X-Email")
                            xCode = response.headers().get("X-Code")
                            _authResult.postValue(Result.Success(true))
                        } else {
                            _authResult.postValue(Result.Error("${response.message()} ${response.code()}"))
                        }
                    }

                    override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _authResult.postValue(Result.Error(t.message ?: "Something went wrong"))
                    }
                })
        }
    }


    fun verifyCodeTelegram(codeFromTg: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callVerificationEndpointTelegram(codeFromTg, Dispatchers.Default) }
    }

    private suspend fun callVerificationEndpointTelegram(
        code: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.verificationWithTelegram(xId, xCode, VerificationRequest(code = code))
                .enqueue(object : Callback<VerificationResponse> {
                    override fun onResponse(
                        call: Call<VerificationResponse>,
                        response: Response<VerificationResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            token = response.body()?.token
                            if (token == null) {
                                _verifyResult.postValue(Result.Error("token == null!!!!!"))
                            } else {
                                _verifyResult.postValue(
                                    Result.Success(
                                        UserData(
                                            token,
                                            telegramOrEmail
                                        )
                                    )
                                )
                                updatePushToken()
                            }
                        } else {
                            _verifyResult.postValue(Result.Error(response.message() + " " + response.code()))
                        }
                    }

                    override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _verifyResult.postValue(Result.Error(t.message ?: "Something went wrong"))
                    }
                })
        }
    }

    private fun updatePushToken() {
        val token = sharedPreferences.pushToken
        if (token != null) {
            val deviceId = Settings.Secure.getString(
                app.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            viewModelScope.launch {
                notificationsRepository.updatePushToken(
                    PushTokenEntity(
                        token = token,
                        device = deviceId
                    )
                )
            }
        }

    }

    fun verifyCodeEmail(codeFromTg: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callVerificationEndpointEmail(codeFromTg, Dispatchers.Default) }
    }

    private suspend fun callVerificationEndpointEmail(
        code: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            Log.d("Token", "xEmail:${xEmail} --- xCode:${xCode}---- verifyCode:${code} ")
            thanksApi.verificationWithEmail(xEmail, xCode, VerificationRequest(code = code))
                .enqueue(object : Callback<VerificationResponse> {
                    override fun onResponse(
                        call: Call<VerificationResponse>,
                        response: Response<VerificationResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            token = response.body()?.token
                            if (token == null) {
                                _verifyResult.postValue(Result.Error("token == null!!!!!"))

                            } else {
                                _verifyResult.postValue(
                                    Result.Success(
                                        UserData(
                                            token,
                                            telegramOrEmail
                                        )
                                    )
                                )
                            }
                        } else {
                            _verifyResult.postValue(Result.Error(response.message() + " " + response.code()))

                        }
                    }

                    override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _verifyResult.postValue(Result.Error(t.message ?: "Something went wrong"))
                    }
                })
        }
    }
}

sealed class AuthorizationType {
    object Email : AuthorizationType()
    object Telegram : AuthorizationType()
}
