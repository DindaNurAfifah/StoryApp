package com.dicoding.storyapplast.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapplast.data.preference.SharedPrefModel
import com.dicoding.storyapplast.data.StoriesRepository
import com.dicoding.storyapplast.data.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StoriesRepository) : ViewModel() {

    fun getToken(): LiveData<SharedPrefModel> {
        return repository.getToken().asLiveData()
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> = repository.getStories(token).cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}