package com.teamforce.thanksapp.data.sources

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.teamforce.thanksapp.utils.Consts

class BasePagingSource<V : Any>(
    private val totalPages: Int? = null,
    private val block: suspend (limit: Int) -> List<V>
) : PagingSource<Int, V>() {
    override fun getRefreshKey(state: PagingState<Int, V>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, V> {
        var page = params.key ?: 1

        if (params is LoadParams.Refresh) {
            page = 1
        }
        return try {
            val response = block(page)

            val nextKey =
                if (response.isEmpty()) {
                    null
                } else {
                    page + (params.loadSize / Consts.PAGE_SIZE)
                }


            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

fun <V : Any> createPager(
    totalPages: Int? = null,
    pageSize: Int = Consts.PAGE_SIZE,
    enablePlaceholders: Boolean = false,
    block: suspend (Int) -> List<V>
): Pager<Int, V> = Pager(
    config = PagingConfig(
        initialLoadSize = Consts.PAGE_SIZE,
        enablePlaceholders = enablePlaceholders,
        pageSize = pageSize,
        prefetchDistance = 2,
    ),
    pagingSourceFactory = { BasePagingSource(totalPages, block) }
)