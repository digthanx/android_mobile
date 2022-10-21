package com.teamforce.thanksapp.data.sources.challenge

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.model.domain.ChallengeModel
import com.teamforce.thanksapp.model.domain.ChallengeModelById
import com.teamforce.thanksapp.utils.Consts
import retrofit2.HttpException
import java.io.IOException

class ChallengePagingSource(
    private val api: ThanksApi,
) : PagingSource<Int, ChallengeModel>() {

    override fun getRefreshKey(state: PagingState<Int, ChallengeModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChallengeModel> {
        var pageIndex = params.key ?: 1

        if (params is LoadParams.Refresh) {
            pageIndex = 1
        }

        return try {
            val response = api.getChallengesWithInfinityScroll(
                limit = Consts.PAGE_SIZE,
                offset = pageIndex,
            )
            val nextKey =
                if (response.isEmpty()) {
                    null
                } else {
                    pageIndex + (params.loadSize / Consts.PAGE_SIZE)
                }
            LoadResult.Page(
                data = response,
                prevKey = if (pageIndex == 1) null else pageIndex,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

}