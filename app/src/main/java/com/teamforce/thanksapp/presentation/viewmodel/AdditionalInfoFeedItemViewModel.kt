package com.teamforce.thanksapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.CreateCommentRequest
import com.teamforce.thanksapp.data.request.GetCommentsRequest
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.data.response.GetCommentsResponse
import com.teamforce.thanksapp.model.domain.CommentModel
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
class AdditionalInfoFeedItemViewModel @Inject constructor(
    private val thanksApi: ThanksApi,
    val userDataRepository: UserDataRepository
    ) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _comments = MutableLiveData<GetCommentsResponse?>()
    val comments: LiveData<GetCommentsResponse?> = _comments
    private val _commentsLoadingError = MutableLiveData<String>()
    val commentsLoadingErorr: LiveData<String> = _commentsLoadingError


    private val _deleteComment = MutableLiveData<CancelTransactionResponse>()
    val deleteComment: LiveData<CancelTransactionResponse> = _deleteComment
    private val _deleteCommentLoadingError = MutableLiveData<String>()
    val deleteCommentLoadingErorr: LiveData<String> = _deleteCommentLoadingError
    private val _deleteCommentLoading = MutableLiveData<Boolean>()
    val deleteCommentLoading: LiveData<Boolean> = _deleteCommentLoading


    private val _createCommentsLoadingError = MutableLiveData<String>()
    val createCommentsLoadingErorr: LiveData<String> = _createCommentsLoadingError
    private val _createCommentsLoading = MutableLiveData<Boolean>()
    val createCommentsLoading: LiveData<Boolean> = _createCommentsLoading

    private val _pressLikesError = MutableLiveData<String>()
    val pressLikesError: LiveData<String> = _pressLikesError
    private val _isLoadingLikes = MutableLiveData<Boolean>()
    val isLoadingLikes: LiveData<Boolean> = _isLoadingLikes

    fun deleteComment(commentId: Int) {
        _deleteCommentLoading.postValue(true)
        userDataRepository.token?.let { token ->
            viewModelScope.launch {
                deleteCommentEndpoint(token, commentId, Dispatchers.Default)
            }
        }
    }


    private suspend fun deleteCommentEndpoint(
        token: String,
        commentId: Int,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.deleteComment("Token $token", commentId)
                ?.enqueue(object : Callback<CancelTransactionResponse> {
                    override fun onResponse(
                        call: Call<CancelTransactionResponse>,
                        response: Response<CancelTransactionResponse>
                    ) {
                        _deleteCommentLoading.postValue(false)
                        if (response.code() == 200) {
                            _deleteComment.postValue(response.body())
                        } else {
                            _deleteCommentLoadingError.postValue(
                                response.message() + " " + response.code()
                            )
                        }
                    }

                    override fun onFailure(call: Call<CancelTransactionResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _deleteCommentLoadingError.postValue(t.message)
                    }
                })
        }
    }

    fun loadCommentsList(transactionId: Int) {
        _isLoading.postValue(true)
        val getCommentsRequest = GetCommentsRequest(transactionId)
        userDataRepository.token?.let { token ->
            viewModelScope.launch {
                callCommentsListEndpoint(token, getCommentsRequest, Dispatchers.Default)
            }
        }
    }


    private suspend fun callCommentsListEndpoint(
        token: String,
        transactionId: GetCommentsRequest,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.getComments("Token $token", transactionId)
                ?.enqueue(object : Callback<GetCommentsResponse> {
                    override fun onResponse(
                        call: Call<GetCommentsResponse>,
                        response: Response<GetCommentsResponse>
                    ) {
                        _isLoading.postValue(false)
                        if (response.code() == 200) {
                            _comments.postValue(response.body())
                        } else {
                            _commentsLoadingError.postValue(
                                response.message() + " " + response.code()
                            )
                        }
                    }

                    override fun onFailure(call: Call<GetCommentsResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _commentsLoadingError.postValue(t.message)
                    }
                })
        }
    }

    fun addComment(transactionId: Int, text: String) {
        val createCommentRequest = CreateCommentRequest(transactionId, text)
        _createCommentsLoading.postValue(true)
        userDataRepository.token?.let { token ->
            viewModelScope.launch {
                addCommentEndpoint(token, createCommentRequest, Dispatchers.Default)
            }
        }
    }


    private suspend fun addCommentEndpoint(
        token: String,
        createCommentRequest: CreateCommentRequest,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi?.createComment("Token $token", createCommentRequest)
                ?.enqueue(object : Callback<CancelTransactionResponse> {
                    override fun onResponse(
                        call: Call<CancelTransactionResponse>,
                        response: Response<CancelTransactionResponse>
                    ) {
                        _createCommentsLoading.postValue(false)
                        if (response.code() == 200) {

                        } else {
                            _createCommentsLoadingError.postValue(
                                response.message() + " " + response.code()
                            )
                        }
                    }

                    override fun onFailure(call: Call<CancelTransactionResponse>, t: Throwable) {
                        _isLoading.postValue(false)
                        _createCommentsLoadingError.postValue(t.message)
                    }
                })
        }
    }

    fun pressLike(mapReactions: Map<String, Int>) {
        _isLoadingLikes.postValue(true)
        userDataRepository.token?.let {
            viewModelScope.launch { callPressLikeEndpoint(it, mapReactions, Dispatchers.IO) }
        }

    }

    private suspend fun callPressLikeEndpoint(
        token: String,
        listReactions: Map<String, Int>,
        dispatcher: CoroutineDispatcher
    ) {
        withContext(dispatcher) {
            thanksApi.pressLike("Token $token", listReactions)
                .enqueue(object : Callback<CancelTransactionResponse> {
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