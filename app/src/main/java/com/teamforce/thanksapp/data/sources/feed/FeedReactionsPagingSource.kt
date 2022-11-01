package com.teamforce.thanksapp.data.sources.feed

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.GetReactionsForTransactionRequest
import com.teamforce.thanksapp.data.response.GetReactionsForTransactionsResponse
import com.teamforce.thanksapp.utils.Consts
import retrofit2.HttpException
import java.io.IOException

class FeedReactionsPagingSource(
    private val api: ThanksApi,
    private val transactionId: Int
) : PagingSource<Int, GetReactionsForTransactionsResponse.InnerInfoLike>() {

    override fun getRefreshKey(state: PagingState<Int, GetReactionsForTransactionsResponse.InnerInfoLike>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GetReactionsForTransactionsResponse.InnerInfoLike> {
        var pageIndex = params.key ?: 0

//        if (params is LoadParams.Refresh) {
//            pageIndex = 1
//        }

        return try {
            val response = api.getReactionsForTransaction(
                GetReactionsForTransactionRequest(
                    transaction_id = transactionId,
                    limit = Consts.PAGE_SIZE,
                    offset = pageIndex
                )
            )
            val nextKey =
                if (response.likes[0].items.isEmpty()) {
                    null
                } else {
                    pageIndex + (Consts.PAGE_SIZE)
                }
            LoadResult.Page(
                data = response.likes[0].items,
                prevKey = if (pageIndex == 0) null else pageIndex,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

}