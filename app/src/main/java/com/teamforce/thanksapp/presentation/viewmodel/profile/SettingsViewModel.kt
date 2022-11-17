package com.teamforce.thanksapp.presentation.viewmodel.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.entities.profile.OrganizationModel
import com.teamforce.thanksapp.data.response.ChangeOrgResponse
import com.teamforce.thanksapp.domain.repositories.ProfileRepository
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
class SettingsViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val userDataRepository: UserDataRepository,
): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    private val _organisations = MutableLiveData<List<OrganizationModel>?>()
    val organizations: LiveData<List<OrganizationModel>?> = _organisations
    private val _organisationsError = MutableLiveData<String>()
    val organizationsError: LiveData<String> = _organisationsError

    private val _authResult = MutableLiveData<Boolean>()
    val authResult: LiveData<Boolean> = _authResult

    var xCode: String? = null
    var orgCode: String? = null
    var xId: String? = null
    private var xEmail: String? = null
    var authorizationType: ProfileViewModel.AuthorizationType? = null

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
                .enqueue(object : Callback<ChangeOrgResponse> {
                    override fun onResponse(
                        call: Call<ChangeOrgResponse>,
                        response: Response<ChangeOrgResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Status запроса: ${response.body().toString()}")
                            if (response.body()?.status == "Код для подтверждения смены организации отправлен в телеграм") {
                                xId = response.headers().get("tg_id")
                                authorizationType = ProfileViewModel.AuthorizationType.Telegram
                            }
                            if (response.body()
                                    .toString() == "{status=Код для подтверждения смены организации отправлен на указанную электронную почту}"
                            ) {
                                xEmail = response.headers().get("X-Email")
                                authorizationType = ProfileViewModel.AuthorizationType.Email
                            }
                            xCode = response.headers().get("X-Code")
                            orgCode = response.headers().get("organization_id")
                            _authResult.postValue(true)
                        } else {
                            _authResult.postValue(false)
                        }
                    }

                    override fun onFailure(call: Call<ChangeOrgResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _authResult.postValue(false)
                    }
                })
        }
    }


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

    fun saveCredentialsForChangeOrg(){
        userDataRepository.saveCredentialsForChangeOrg(
            xCode = xCode, xId = xId, orgCode = orgCode)
    }
}