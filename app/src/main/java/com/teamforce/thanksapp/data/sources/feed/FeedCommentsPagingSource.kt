package com.teamforce.thanksapp.data.sources.feed

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teamforce.thanksapp.data.api.ThanksApi
import com.teamforce.thanksapp.data.request.GetCommentsRequest
import com.teamforce.thanksapp.model.domain.CommentModel
import com.teamforce.thanksapp.utils.Consts
import retrofit2.HttpException
import java.io.IOException

class FeedCommentsPagingSource(
    private val api: ThanksApi,
    private val transactionId: Int
) : PagingSource<Int, CommentModel>() {

    override fun getRefreshKey(state: PagingState<Int, CommentModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentModel> {
        var pageIndex = params.key ?: 0

//        if (params is LoadParams.Refresh) {
//            pageIndex = 0
//        }

        return try {
            val response = api.getComments(
                GetCommentsRequest(
                    transaction_id = transactionId,
                    limit = Consts.PAGE_SIZE,
                    offset = pageIndex
                )
            )
            val nextKey =
                if (response.comments.isEmpty()) {
                    null
                } else {
                    pageIndex + (Consts.PAGE_SIZE)
                }
            LoadResult.Page(
                data = response.comments,
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