package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.ProfileResponse
import com.teamforce.thanksapp.data.response.UserTransactionsResponse
import com.teamforce.thanksapp.model.domain.HistoryModel
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class HistoryViewModel : ViewModel() {

    private var thanksApi: ThanksApi? = null
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

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }

    fun loadTransactionsList(token: String, user: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callTransactionsListEndpoint(token, user, Dispatchers.Default) }
    }

    fun loadUserProfile(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callProfileEndpoint(token, Dispatchers.Default) }
    }

    private suspend fun callProfileEndpoint(
        token: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.getProfile("Token $token")?.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _profile.postValue(response.body())
                    } else {
                        _profileError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    _isLoading.postValue(false)
                    _profileError.postValue(t.message)
                }
            })
        }
    }

    private suspend fun callTransactionsListEndpoint(
        token: String,
        user: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.getUserTransactions("Token $token")
                ?.enqueue(object : Callback<List<UserTransactionsResponse>> {
                    override fun onResponse(
                        call: Call<List<UserTransactionsResponse>>,
                        response: Response<List<UserTransactionsResponse>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            val allData: SparseArray<HistoryModel> = SparseArray<HistoryModel>()
                            val receivedData: SparseArray<HistoryModel> = SparseArray<HistoryModel>()
                            val sendedData: SparseArray<HistoryModel> = SparseArray<HistoryModel>()
                            val history: List<UserTransactionsResponse>? = response.body()
                            if (history != null) {
                                for (item in history) {
                                    try {
                                        val dateTime: LocalDateTime =
                                            LocalDateTime.parse(item.updatedAt.replace("+03:00", ""))
                                        val day = dateTime.dayOfYear

                                        if (allData.containsKey(day)) {
                                            val model = allData.get(day)
                                            var data = model.data
                                            data = data.plusElement(item)
                                            allData.put(day, HistoryModel(day, data))
                                        } else {
                                            allData.put(day, HistoryModel(day, listOf(item)))
                                        }

                                        if (item.sender.sender_tg_name.equals(user)) {
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
                                                receivedData.put(day, HistoryModel(day, listOf(item)))
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

                    override fun onFailure(call: Call<List<UserTransactionsResponse>>, t: Throwable) {
                        _isLoading.postValue(false)
                        _transactionsLoadingError.postValue(t.message)
                    }
                })
        }
    }
}
