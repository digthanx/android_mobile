package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CancelTransactionRequest
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.UserTransactionsResponse
import com.teamforce.thanksapp.model.domain.HistoryModel
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _allTransactions = MutableLiveData<SparseArray<HistoryModel>>()
    val allTransactions: LiveData<SparseArray<HistoryModel>> = _allTransactions
    private val _receivedTransactions = MutableLiveData<SparseArray<HistoryModel>>()
    val receivedTransactions: LiveData<SparseArray<HistoryModel>> = _receivedTransactions
    private val _sentTransactions = MutableLiveData<SparseArray<HistoryModel>>()
    val sentTransactions: LiveData<SparseArray<HistoryModel>> = _sentTransactions
    private val _transactionsLoadingError = MutableLiveData<String>()
    val transactionsLoadingError: LiveData<String> = _transactionsLoadingError

    private val _profile = MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> = _profile
    private val _profileError = MutableLiveData<String>()
    val profileError: LiveData<String> = _profileError

    private val _cancelTransaction = MutableLiveData<CancelTransactionResponse>()
    val cancelTransaction: LiveData<CancelTransactionResponse> = _cancelTransaction
    private val _cancelTransactionError = MutableLiveData<String>()
    val cancelTransactionError: LiveData<String> = _cancelTransactionError

    fun loadTransactionsList() {
        _isLoading.postValue(true)
        viewModelScope.launch { callTransactionsListEndpoint(userDataRepository.getUserName()!!, Dispatchers.Default) }
    }

    private suspend fun callTransactionsListEndpoint(
        user: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi.getUserTransactions()
                .enqueue(object : Callback<List<UserTransactionsResponse>> {
                    override fun onResponse(
                        call: Call<List<UserTransactionsResponse>>,
                        response: Response<List<UserTransactionsResponse>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            val allData: SparseArray<HistoryModel> = SparseArray<HistoryModel>()
                            val receivedData: SparseArray<HistoryModel> =
                                SparseArray<HistoryModel>()
                            val sendedData: SparseArray<HistoryModel> = SparseArray<HistoryModel>()
                            val history: List<UserTransactionsResponse>? = response.body()
                            if (history != null) {
                                for (item in history) {
                                    try {
                                        val dateTime: LocalDateTime =
                                            LocalDateTime.parse(
                                                item.updatedAt.replace(
                                                    "+03:00",
                                                    ""
                                                )
                                            )
                                        val day = dateTime.dayOfYear

                                        if (allData.containsKey(day)) {
                                            val model = allData.get(day)
                                            var data = model.data
                                            data = data.plusElement(item)
                                            allData.put(day, HistoryModel(day, data))
                                        } else {
                                            allData.put(day, HistoryModel(day, listOf(item)))
                                        }
                                        if (item.sender.sender_tg_name != null && item.sender.sender_tg_name.equals(
                                                user
                                            )
                                        ) {
                                            if (sendedData.containsKey(day)) {
                                                val model = sendedData.get(day)
                                                var data = model.data
                                                data = data.plusElement(item)
                                                sendedData.put(day, HistoryModel(day, data))
                                            } else {
                                                sendedData.put(day, HistoryModel(day, listOf(item)))
                                            }
                                        } else {
                                            if (receivedData.containsKey(day)) {
                                                val model = receivedData.get(day)
                                                var data = model.data
                                                data = data.plusElement(item)
                                                receivedData.put(day, HistoryModel(day, data))
                                            } else {
                                                receivedData.put(
                                                    day,
                                                    HistoryModel(day, listOf(item))
                                                )
                                            }
                                        }

                                        _allTransactions.postValue(allData)
                                        _receivedTransactions.postValue(receivedData)
                                        _sentTransactions.postValue(sendedData)
                                    } catch (e: Exception) {
                                        Log.e("HistoryViewModel", e.message, e.fillInStackTrace())
                                    }
                                }
                            } else {
                                _transactionsLoadingError.postValue(response.message() + " " + response.code())
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<List<UserTransactionsResponse>>,
                        t: Throwable
                    ) {
                        _isLoading.postValue(false)
                        _transactionsLoadingError.postValue(t.message)
                    }
                })
        }
    }


    fun cancelUserTransaction(transactionId: String, status: String) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            cancelUserTransactionEndpoint(
                transactionId,
                status,
                Dispatchers.Default
            )
        }
    }

    private suspend fun cancelUserTransactionEndpoint(
        transactionId: String,
        status: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi.cancelTransaction(
                transactionId,
                CancelTransactionRequest(status)
            ).enqueue(object : Callback<CancelTransactionResponse> {
                override fun onResponse(
                    call: Call<CancelTransactionResponse>,
                    response: Response<CancelTransactionResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _cancelTransaction.postValue(response.body())
                    } else {
                        _cancelTransactionError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<CancelTransactionResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _cancelTransactionError.postValue(t.message)
                }
            })
        }
    }
}
