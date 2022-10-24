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
            LoadResult.Page(
                data = response,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (totalPages != null && page == totalPages) null else page + 1
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
    config = PagingConfig(enablePlaceholders = enablePlaceholders, pageSize = pageSize),
    pagingSourceFactory = { BasePagingSource(totalPages, block) }
)