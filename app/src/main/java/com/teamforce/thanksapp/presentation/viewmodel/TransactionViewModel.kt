package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.SendCoinsRequest
import com.teamforce.thanksapp.data.request.UsersListRequest
import com.teamforce.thanksapp.data.response.SendCoinsResponse
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionViewModel : ViewModel() {

    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _users = MutableLiveData<List<UserBean>>()
    val users: LiveData<List<UserBean>> = _users
    private val _usersLoadingError = MutableLiveData<String>()
    val usersLoadingError: LiveData<String> = _usersLoadingError
    private val _isSuccessOperation = MutableLiveData<Boolean>()
    val isSuccessOperation: LiveData<Boolean> = _isSuccessOperation
    private val _sendCoinsError = MutableLiveData<String>()
    val sendCoinsError: LiveData<String> = _sendCoinsError

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }

    fun loadUsersList(username: String, token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUsersListEndpoint(username, token, Dispatchers.Default) }
    }

    fun sendCoins(token: String, recipient: Int, amount: Int, reason: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callSendCoinsEndpoint(token, recipient, amount, reason, Dispatchers.Default)
        }
    }

    private suspend fun callUsersListEndpoint(
        username: String,
        token: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.getUsersList("Token $token", UsersListRequest(username))
                ?.enqueue(object : Callback<List<UserBean>> {
                    override fun onResponse(
                        call: Call<List<UserBean>>,
                        response: Response<List<UserBean>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            _users.postValue(response.body())
                        } else {
                            _usersLoadingError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<List<UserBean>>, t: Throwable) {
                        _isLoading.postValue(false)
                        _usersLoadingError.postValue(t.message)
                    }
                })
        }
    }

    private suspend fun callSendCoinsEndpoint(
        token: String,
        recipient: Int,
        amount: Int,
        reason: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.sendCoins("Token $token", SendCoinsRequest(recipient, amount, reason))
                ?.enqueue(object : Callback<SendCoinsResponse> {
                    override fun onResponse(
                        call: Call<SendCoinsResponse>,
                        response: Response<SendCoinsResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 201) {
                            _isSuccessOperation.postValue(true)
                        } else {
                            _sendCoinsError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<SendCoinsResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _sendCoinsError.postValue(t.message)
                    }
                })
        }
    }
}
