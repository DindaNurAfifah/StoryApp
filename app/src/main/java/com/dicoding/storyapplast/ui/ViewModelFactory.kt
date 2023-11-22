package com.dicoding.storyapplast.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapplast.data.Injection
import com.dicoding.storyapplast.data.StoriesRepository
import com.dicoding.storyapplast.ui.Login.MainViewModel
import com.dicoding.storyapplast.ui.home.HomeViewModel
import com.dicoding.storyapplast.ui.home.map.MapsViewModel
import com.dicoding.storyapplast.ui.home.upload.UploadViewModel
import com.dicoding.storyapplast.ui.register.RegistrasiViewModel

class ViewModelFactory(private val  repository: StoriesRepository): ViewModelProvider.NewInstanceFactory() {

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.getRepository(context))
            }.also { instance = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegistrasiViewModel::class.java) -> {
                RegistrasiViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("The viewModel Class : " + modelClass.name + " is nowhere to be found")
        }
    }
}