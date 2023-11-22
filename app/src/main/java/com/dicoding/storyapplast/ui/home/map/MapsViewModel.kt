package com.dicoding.storyapplast.ui.home.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapplast.data.preference.SharedPrefModel
import com.dicoding.storyapplast.data.StoriesRepository

class MapsViewModel(private val repository: StoriesRepository) : ViewModel() {

    val stories = repository.stories

    fun getStoriesLocation(token: String) = repository.getStoriesLocation(token)

    fun getToken(): LiveData<SharedPrefModel> {
        return repository.getToken().asLiveData()
    }
}