package com.dicoding.storyapplast.ui.home

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapplast.data.response.ListStoryItem
import com.dicoding.storyapplast.data.retrofit.ApiService

class HomePagingSource(private val apiService: ApiService, val token: String) : PagingSource<Int, ListStoryItem>() {

    companion object {
        const val TAG = "StoriesPagingSource"
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getStories(token, page, params.loadSize)
            LoadResult.Page(
                data = response.listStoryItem,
                prevKey = if(page == 1) null else page -1,
                nextKey = if(response.listStoryItem.isEmpty()) null else page +1
            )
        } catch(e: Exception) {
            Log.e(TAG, "getStories: ${e.localizedMessage}")
            return LoadResult.Error(e)
        }
    }

}