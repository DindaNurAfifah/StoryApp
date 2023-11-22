package com.dicoding.storyapplast.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapplast.data.body.dataLogin
import com.dicoding.storyapplast.data.body.dataRegistrasi
import com.dicoding.storyapplast.data.preference.SharedPref
import com.dicoding.storyapplast.data.preference.SharedPrefModel
import com.dicoding.storyapplast.data.response.ListStoryItem
import com.dicoding.storyapplast.data.response.RegisterResponse
import com.dicoding.storyapplast.data.response.StoriesResponse
import com.dicoding.storyapplast.data.response.UpStoriesResponse
import com.dicoding.storyapplast.data.retrofit.ApiService
import com.dicoding.storyapplast.ui.home.HomePagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoriesRepository(private val apiService: ApiService, private val sharedPref: SharedPref){

    companion object {
        const val TAG_MAPS = "REPOSITORY.getStoriesLocation"
        const val TAG_UPLOAD = "REPOSITORY.setStories"
        @Volatile
        private var instance: StoriesRepository? = null
        fun getInstance(apiService: ApiService, sharedPref: SharedPref): StoriesRepository =
            instance ?: synchronized(this) {
                instance ?: StoriesRepository(apiService, sharedPref)
            }.also { instance = it }
    }

    private val _stories = MutableLiveData<List<ListStoryItem>> ()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _detailStories = MutableLiveData<ListStoryItem>()
    val detailStories: LiveData<ListStoryItem> = _detailStories


    private val _maps = MediatorLiveData<Result<List<ListStoryItem>>>()
    val maps : LiveData<Result<List<ListStoryItem>>> = _maps

    private val _upStories = MutableLiveData<Result<UpStoriesResponse>> ()

    suspend fun setToken(shared: SharedPrefModel) {
        sharedPref.setToken(shared)
    }

    fun getToken() : Flow<SharedPrefModel> {
        return sharedPref.getToken()
    }

    suspend fun getLogin(data: dataLogin) =
        apiService.getLogin(data)

    suspend fun getRegister(data: dataRegistrasi): RegisterResponse{
        return apiService.getRegister(data)
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            pagingSourceFactory = {
                HomePagingSource(apiService, " Bearer $token")
            }
        ).liveData
    }

    fun getStoriesLocation(token: String) {
        val response = apiService.getStoriesLocation("Bearer $token")
        response.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _stories.value = response.body()?.listStoryItem
                } else {
                    Log.e(TAG_MAPS, "failed: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                Log.e(TAG_MAPS, "failed: ${t.message.toString()}" )
            }
        })
    }

    suspend fun setStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Result<UpStoriesResponse>> {
        try {
            apiService.setStories("Bearer $token", file, description, lat, lon)
        } catch (e: Exception) {
            Log.e(TAG_UPLOAD, "failed: ${e.message.toString()}")
        }
        return _upStories
    }

    suspend fun logout() = sharedPref.clearToken()
}