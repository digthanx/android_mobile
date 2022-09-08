package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.BalanceResponse
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
class BalanceViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _balance = MutableLiveData<BalanceResponse>()
    val balance: LiveData<BalanceResponse> = _balance
    private val _balanceError = MutableLiveData<String>()
    val balanceError: LiveData<String> = _balanceError


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
                        Log.d("Token", "Пользовательские данные ${response.body()}")
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
}
