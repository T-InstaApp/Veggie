package com.instaapp.veggietemp1.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.instaapp.veggietemp1.network.MyApi
import com.instaapp.veggietemp1.network.responseModel.MenuResponseData
import com.instaapp.veggietemp1.utils.log

class DataPagingSource(
    private val apiService: MyApi,
    private val catID: Int,
    private val restID: String,
    private val token: String
) : PagingSource<Int, MenuResponseData>() {

    override fun getRefreshKey(state: PagingState<Int, MenuResponseData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MenuResponseData> {
        return try {
            val position = params.key ?: FIRST_PAGE_INDEX
            log("DataPagingSource ", " $restID = $catID =$position")
            val responseData = apiService.getMenuList2(token, restID, catID, position)
            LoadResult.Page(
                data = responseData.results!!,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == responseData.count) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}