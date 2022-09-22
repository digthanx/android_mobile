package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.utils.RetrofitClient
import com.teamforce.thanksapp.utils.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdditionalInfoFeedItemViewModel(): ViewModel() {

    private var thanksApi: ThanksApi? = null


    private val _pressLikesError = MutableLiveData<String>()
    val pressLikesError: LiveData<String> = _pressLikesError
    private val _isLoadingLikes = MutableLiveData<Boolean>()
    val isLoadingLikes: LiveData<Boolean> = _isLoadingLikes

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }


    fun pressLike(mapReactions: Map<String, Int>) {
        _isLoadingLikes.postValue(true)
        UserDataRepository.getInstance()?.token?.let {
            viewModelScope.launch { callPressLikeEndpoint(it, mapReactions, Dispatchers.IO) }
        }

    }

    private suspend fun callPressLikeEndpoint(
        token: String,
        listReactions: Map<String, Int>,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.pressLike("Token $token", listReactions)
                ?.enqueue(object : Callback<CancelTransactionResponse> {
                    override fun onResponse(
                        call: Call<CancelTransactionResponse>,
                        response: Response<CancelTransactionResponse>
                    ) {
                        _isLoadingLikes.postValue(false)
                        if (response.code() == 200) {
                            Log.d("Token", "Успешно лайк отправил")
                        } else {
                            _pressLikesError.postValue(response.message() + " " + response.code())
                        }
                    }

                    override fun onFailure(call: Call<CancelTransactionResponse>, t: Throwable) {
                        _isLoadingLikes.postValue(false)
                        _pressLikesError.postValue(t.message)
                    }
                })
        }
    }
}