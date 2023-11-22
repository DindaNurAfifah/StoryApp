package com.dicoding.storyapplast.data

import android.content.Context
import com.dicoding.storyapplast.data.preference.SharedPref
import com.dicoding.storyapplast.data.preference.dataStore
import com.dicoding.storyapplast.data.retrofit.ApiConfig

object Injection {
    fun getRepository(context: Context): StoriesRepository {
        val shared = SharedPref.getInnstance(context.dataStore)
        val apiService = ApiConfig.apiService
        return StoriesRepository.getInstance(apiService, shared)
    }
}