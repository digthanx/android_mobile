package com.teamforce.thanksapp.presentation.viewmodel.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamforce.thanksapp.data.response.CancelTransactionResponse
import com.teamforce.thanksapp.data.response.GetChallengeCommentsResponse
import com.teamforce.thanksapp.data.response.GetCommentsResponse
import com.teamforce.thanksapp.domain.repositories.ChallengeRepository
import com.teamforce.thanksapp.utils.ResultWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CommentsChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository
): ViewModel(){

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _comments = MutableLiveData<GetChallengeCommentsResponse?>()
    val comments: LiveData<GetChallengeCommentsResponse?> = _comments
    private val _commentsLoadingError = MutableLiveData<String>()
    val commentsLoadingErorr: LiveData<String> = _commentsLoadingError


    private val _deleteComment = MutableLiveData<CancelTransactionResponse>()
    val deleteComment: LiveData<CancelTransactionResponse> = _deleteComment
    private val _deleteCommentLoadingError = MutableLiveData<String>()
    val deleteCommentLoadingErorr: LiveData<String> = _deleteCommentLoadingError
    private val _deleteCommentLoading = MutableLiveData<Boolean>()
    val deleteCommentLoading: LiveData<Boolean> = _deleteCommentLoading


    private val _createCommentsLoadingError = MutableLiveData<String>()
    val createCommentsLoadingError: LiveData<String> = _createCommentsLoadingError
    private val _createCommentsLoading = MutableLiveData<Boolean>()
    val createCommentsLoading: LiveData<Boolean> = _createCommentsLoading


    fun loadComments(
        challengeId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = challengeRepository.loadChallengeComments(challengeId)) {
                    is ResultWrapper.Success -> {
                        _comments.postValue(result.value)
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _commentsLoadingError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _commentsLoadingError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun createComment(
        challengeId: Int,
        text: String
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = challengeRepository.createChallengeComment(challengeId, text)) {
                    is ResultWrapper.Success -> {
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _commentsLoadingError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _commentsLoadingError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteComment(
        commentId: Int
    ) {
        _isLoading.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = challengeRepository.deleteChallengeComment(commentId)) {
                    is ResultWrapper.Success -> {
                    }
                    else -> {
                        if (result is ResultWrapper.GenericError) {
                            _deleteCommentLoadingError.postValue(result.error + " " + result.code)

                        } else if (result is ResultWrapper.NetworkError) {
                            _deleteCommentLoadingError.postValue("Ошибка сети")
                        }
                    }
                }
                _isLoading.postValue(false)
            }
        }
    }



}