package com.teamforce.thanksapp.presentation.viewmodel.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.teamforce.thanksapp.data.response.GetReactionsForTransactionsResponse
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FragmentReactionsFeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository
): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _reactions = MutableLiveData<GetReactionsForTransactionsResponse.InnerInfoLike?>()
    val reactions: LiveData<GetReactionsForTransactionsResponse.InnerInfoLike?> = _reactions
    private val _reactionsLoadingError = MutableLiveData<String>()
    val reactionsLoadingError: LiveData<String> = _reactionsLoadingError


    fun loadReactions(
        transactionId: Int
    ): Flow<PagingData<GetReactionsForTransactionsResponse.InnerInfoLike>> {
        return feedRepository.getReactions(
            transactionId = transactionId
        ).map { it }
    }
}