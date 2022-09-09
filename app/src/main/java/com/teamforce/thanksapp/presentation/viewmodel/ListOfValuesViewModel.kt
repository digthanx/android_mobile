package com.teamforce.thanksapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.model.domain.TagModel
import com.teamforce.thanksapp.utils.RetrofitClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListOfValuesViewModel : ViewModel() {

    private var thanksApi: ThanksApi? = null
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _tags = MutableLiveData<List<TagModel>>()
    val tags: LiveData<List<TagModel>> = _tags
    private val _tagsError = MutableLiveData<String>()
    val tagsError: LiveData<String> = _tagsError

    fun initViewModel() {
        thanksApi = RetrofitClient.getInstance()
    }

    fun loadTags(token: String) {
        _isLoading.postValue(true)
        viewModelScope.launch { callTagsEndpoint(token, Dispatchers.Default) }
    }

    private suspend fun callTagsEndpoint(
        token: String,
        coroutineDispatcher: CoroutineDispatcher
    ) {
        withContext(coroutineDispatcher) {
            thanksApi?.getTags("Token $token")?.enqueue(object : Callback<List<TagModel>> {
                override fun onResponse(
                    call: Call<List<TagModel>>,
                    response: Response<List<TagModel>>
                ) {
                    _isLoading.postValue(false)
                    if (response.code() == 200) {
                        _tags.postValue(response.body())
                    } else {
                        _tagsError.postValue(response.message() + " " + response.code())
                    }
                }

                override fun onFailure(call: Call<List<TagModel>>, t: Throwable) {
                    _isLoading.postValue(false)
                    _tagsError.postValue(t.message)
                }
            })
        }
    }
}