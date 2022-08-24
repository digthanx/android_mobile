package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.SendCoinsRequest
import com.teamforce.thanksapp.data.request.UserListWithoutInputRequest
import com.teamforce.thanksapp.data.request.UsersListRequest
import com.teamforce.thanksapp.data.response.BalanceResponse
import com.teamforce.thanksapp.data.response.SendCoinsResponse
import com.teamforce.thanksapp.data.response.UserBean
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.*
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
    private val _balance = MutableLiveData<BalanceResponse>()
    val balance: LiveData<BalanceResponse> = _balance
    private val _balanceError = MutableLiveData<String>()
    val balanceError: LiveData<String> = _balanceError

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }



    fun loadUserBalance(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callBalanceEndpoint(token, Dispatchers.Default) }
    }

    private suspend fun callBalanceEndpoint(
        token: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.getBalance("Token $token")?.enqueue(object : Callback<BalanceResponse> {
                override fun onResponse(
                    call: Call<BalanceResponse>,
                    response: Response<BalanceResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _balance.postValue(response.body())
                    } else {
                        _balanceError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<BalanceResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _balanceError.postValue(t.message)
                }
            })
        }
    }



    fun loadUsersList(username: String, token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUsersListEndpoint(username, token, Dispatchers.Default) }
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
                            Log.d("Token", "Кому спасибки ${response.body()}")
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



    fun sendCoins(token: String, recipient: Int, amount: Int, reason: String, isAnon: Boolean) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            callSendCoinsEndpoint(token, recipient, amount, reason, isAnon, Dispatchers.Default)
        }
    }


    private suspend fun callSendCoinsEndpoint(
        token: String,
        recipient: Int,
        amount: Int,
        reason: String,
        isAnon: Boolean,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.sendCoins("Token $token", SendCoinsRequest(recipient, amount, reason, isAnon))
                ?.enqueue(object : Callback<SendCoinsResponse> {
                    override fun onResponse(
                        call: Call<SendCoinsResponse>,
                        response: Response<SendCoinsResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 201) {
                            _isSuccessOperation.postValue(true)
                        } else if(response.code() == 400) {
                            _sendCoinsError.postValue("Нельзя перевести больше 50% от имеющейся под распределение суммы")
                        }else{
                            Log.d("Token", "Я в эксепшене ${response.body()}")
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

    fun loadUsersListWithoutInput(get_users: String, token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callUsersListWithoutInputEndpoint(get_users, token, Dispatchers.Default) }
    }

    private suspend fun callUsersListWithoutInputEndpoint(
        get_users: String,
        token: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.getUsersWithoutInput("Token $token",
                get_users = UserListWithoutInputRequest(get_users))
                ?.enqueue(object : Callback<List<UserBean>> {
                    override fun onResponse(
                        call: Call<List<UserBean>>,
                        response: Response<List<UserBean>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Кому спасибки ${response.body()}")
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



}
