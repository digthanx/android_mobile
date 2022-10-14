package com.teamforce.thanksapp.presentation.viewmodel.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.teamforce.thanksapp.data.response.FeedResponse
import com.teamforce.thanksapp.domain.repositories.FeedRepository
import com.teamforce.thanksapp.utils.UserDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class FeedListViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val feedRepository: FeedRepository
) : ViewModel() {

    fun getUsername(): String {
        return userDataRepository.getUserName()!!
    }

    fun getFeed(
        mineOnly: Int,
        publicOnly: Int
    ): Flow<PagingData<FeedResponse>> {
        return feedRepository.getFeed(
            mineOnly = if (mineOnly == -1) null else mineOnly,
            publicOnly = if (publicOnly == -1) null else publicOnly
        ).cachedIn(viewModelScope)
    }

}