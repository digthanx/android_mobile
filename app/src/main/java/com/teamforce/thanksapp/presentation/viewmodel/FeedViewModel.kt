package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.FeedResponse
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

class FeedViewModel: ViewModel() {
    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _allFeeds = MutableLiveData<List<FeedResponse>?>()
    val allFeeds: LiveData<List<FeedResponse>?> = _allFeeds
    private val _myFeeds = MutableLiveData<ArrayList<FeedResponse>>()
    val myFeeds: LiveData<ArrayList<FeedResponse>> = _myFeeds
    private val _publicFeeds = MutableLiveData<ArrayList<FeedResponse>>()
    val publicFeeds: LiveData<ArrayList<FeedResponse>> = _publicFeeds
    private val _feedsLoadingError = MutableLiveData<String>()
    val feedsLoadingError: LiveData<String> = _feedsLoadingError

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }

    fun loadFeedsList(token: String, user: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callFeedsListEndpoint(token, user, Dispatchers.Default) }
    }

    private suspend fun callFeedsListEndpoint(
        token: String,
        user: String,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.getFeed("Token $token")
                ?.enqueue(object : Callback<List<FeedResponse>> {
                    override fun onResponse(
                        call: Call<List<FeedResponse>>,
                        response: Response<List<FeedResponse>>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            val myFeeds: ArrayList<FeedResponse> = ArrayList<FeedResponse>()
                            val publicFeeds: ArrayList<FeedResponse> = ArrayList<FeedResponse>()
                            val feeds: List<FeedResponse>? = response.body()
                            if (feeds != null) {
                                for (item in feeds) {
                                    try {
                                        if (item.transaction.sender.equals(user) ||
                                            item.transaction.recipient.equals(user)) {
                                            myFeeds.add(item)
                                        }
                                        if(item.event_type.is_personal) publicFeeds.add(item)

                                        _allFeeds.postValue(feeds)
                                        _myFeeds.postValue(myFeeds)
                                        _publicFeeds.postValue(publicFeeds)
                                    } catch (e: Exception) {
                                        Log.e("FeedViewModel", e.message, e.fillInStackTrace())
                                    }
                                }
                            } else {
                                _feedsLoadingError.postValue(response.message() + " " + response.code())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                        _isLoading.postValue(false)
                        _feedsLoadingError.postValue(t.message)
                    }
                })
        }
    }

}