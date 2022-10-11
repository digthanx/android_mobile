package com.teamforce.thanksapp.data.sources.history

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.response.HistoryItem
import com.teamforce.thanksapp.utils.Consts
import retrofit2.HttpException
import java.io.IOException

class HistoryPagingSource(
    private val api: ThanksApi,
    private val sentOnly: Int?,
    private val receivedOnly: Int?
) : PagingSource<Int, HistoryItem.UserTransactionsResponse>() {

    override fun getRefreshKey(state: PagingState<Int, HistoryItem.UserTransactionsResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryItem.UserTransactionsResponse> {
        var pageIndex = params.key ?: 1

        if (params is LoadParams.Refresh) {
            pageIndex = 1
        }

        return try {
            val response = api.getUserTransactions(
                limit = Consts.PAGE_SIZE,
                offset = pageIndex,
                sentOnly = sentOnly,
                receivedOnly = receivedOnly

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