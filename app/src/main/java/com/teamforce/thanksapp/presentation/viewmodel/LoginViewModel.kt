package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.AuthorizationRequest
import com.teamforce.thanksapp.data.request.VerificationRequest
import com.teamforce.thanksapp.data.response.VerificationResponse
import com.teamforce.thanksapp.model.domain.UserData
import com.teamforce.thanksapp.utils.RetrofitClient
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
    val userDataRepository: UserDataRepository
    ) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isSuccessAuth = MutableLiveData<Boolean>()
    val isSuccessAuth: LiveData<Boolean> = _isSuccessAuth
    private val _authError = MutableLiveData<String>()
    val authError: LiveData<String> = _authError
    private val _verifyError = MutableLiveData<String>()
    val verifyError: LiveData<String> = _verifyError
    private val _verifyResult = MutableLiveData<UserData?>()
    val verifyResult: LiveData<UserData?> = _verifyResult
    private var xId: String? = null
    private var xEmail: String? = null
    private var xCode: String? = null
    private var token: String? = null
    private var telegramOrEmail: String? = null


    fun logout() {
        userDataRepository.logout()
        xId = null
        xEmail = null
        xCode = null
        token = null
        telegramOrEmail = null
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
            thanksApi?.authorization(AuthorizationRequest(login = telegramId))
                ?.enqueue(object : Callback<Any> {
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

                            }
                            userDataRepository.statusResponseAuth =
                                response.body().toString()
                            xCode = response.headers().get("X-Code")
                            _isSuccessAuth.postValue(true)
                        } else {
                            _isSuccessAuth.postValue(false)
                            _authError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        _isLoading.postValue(false)
                        _isSuccessAuth.postValue(false)
                        _authError.postValue(t.message)
                    }
                })
        }
    }


    fun verifyCodeTelegram(telegramId: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callVerificationEndpointTelegram(telegramId, Dispatchers.Default) }
    }

    private suspend fun callVerificationEndpointTelegram(
        code: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.verificationWithTelegram(xId, xCode, VerificationRequest(code = code))
                ?.enqueue(object : Callback<VerificationResponse> {
                    override fun onResponse(
                        call: Call<VerificationResponse>,
                        response: Response<VerificationResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            token = response.body()?.token
                            if (token == null) {
                                _verifyResult.postValue(null)
                                _verifyError.postValue("token == null!!!!!")
                            } else {
                                _verifyResult.postValue(UserData(token, telegramOrEmail))
                            }
                        } else {
                            _verifyResult.postValue(null)
                            _verifyError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _verifyResult.postValue(null)
                        _verifyError.postValue(t.message)
                    }
                })
        }
    }

    fun verifyCodeEmail(code: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callVerificationEndpointEmail(code, Dispatchers.Default) }
    }

    private suspend fun callVerificationEndpointEmail(
        code: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            Log.d("Token", "xEmail:${xEmail} --- xCode:${xCode}---- verifyCode:${code} ")
            thanksApi?.verificationWithEmail(xEmail, xCode, VerificationRequest(code = code))
                ?.enqueue(object : Callback<VerificationResponse> {
                    override fun onResponse(
                        call: Call<VerificationResponse>,
                        response: Response<VerificationResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            token = response.body()?.token
                            if (token == null) {
                                _verifyResult.postValue(null)
                                _verifyError.postValue("token == null!!!!!")
                            } else {
                                _verifyResult.postValue(UserData(token, telegramOrEmail))
                            }
                        } else {
                            _verifyResult.postValue(null)
                            _verifyError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<VerificationResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _verifyResult.postValue(null)
                        _verifyError.postValue(t.message)
                    }
                })
        }
    }


}
