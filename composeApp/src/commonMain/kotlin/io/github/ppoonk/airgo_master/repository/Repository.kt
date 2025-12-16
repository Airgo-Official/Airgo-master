package io.github.ppoonk.airgo_master.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.ppoonk.airgo_master.repository.local.LocalData
import io.github.ppoonk.airgo_master.repository.remote.ApiService

object Repository {
    val local = LocalData
    val remote = ApiService

    fun <T : Any> newPagingSource(
        defaultPageSize: Int = 20,
        fetchData: suspend (Pair<Int, Int>) -> Pair<List<T>, Int>
    ): Pager<Int, T> = Pager(
        config = PagingConfig(
            pageSize = defaultPageSize,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { ACPagingSource(fetchData = fetchData) }
    )
}

class ACPagingSource<T : Any>(
    private val fetchData: suspend (Pair<Int, Int>) -> Pair<List<T>, Int>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                ?: 0
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 0                                // 当前页
            val pageSize = params.loadSize                            // 每页数据条数
            val prevKey = if (page > 0) page - 1 else null            // 上一页

            val res = fetchData(Pair(page, pageSize))

            // total > 当前分页的数据量 并且 total > 当前页面之前的全部数据量
            val nextKey = if (res.second >  pageSize && res.second > (page * pageSize)) page + 1 else null

            LoadResult.Page(
                data = res.first,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(
                Throwable(
                    message = "Failed to load page $params.key: ${e.message}",
                    cause = e
                )
            )
        }
    }
}