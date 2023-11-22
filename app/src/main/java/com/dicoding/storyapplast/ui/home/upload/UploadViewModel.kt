package com.dicoding.storyapplast.ui.home.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapplast.data.preference.SharedPrefModel
import com.dicoding.storyapplast.data.StoriesRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val repository: StoriesRepository) : ViewModel() {


    fun getToken(): LiveData<SharedPrefModel> {
        return repository.getToken().asLiveData()
    }

    suspend fun setStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = repository.setStories(token, file, description, lat, lon)
}