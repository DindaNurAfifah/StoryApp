package com.dicoding.storyapplast.ui.Login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapplast.data.preference.SharedPrefModel
import com.dicoding.storyapplast.data.StoriesRepository
import com.dicoding.storyapplast.data.body.dataLogin
import com.dicoding.storyapplast.data.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: StoriesRepository): ViewModel(){

    companion object{
        private val TAG_MODEL = MainViewModel::class.java.simpleName
        private val TAG_RESPONSE = LoginResponse::class.java
    }

    private val _isLogin = MutableLiveData<LoginResponse>()
    val isLogin: LiveData<LoginResponse> = _isLogin

    private fun setToken(shared: SharedPrefModel) {
        viewModelScope.launch {
            repository.setToken(shared)
        }
    }

    fun getLogin(data: dataLogin) {
        viewModelScope.launch {
            try {
                val response = repository.getLogin(data)
                setToken(
                    SharedPrefModel(
                    response.loginResult.token,
                    response.loginResult.userId,
                    response.loginResult.name,
                    true
                )
                )
                _isLogin.postValue(response)
            } catch (e: HttpException) {
                val response = e.response()?.errorBody()?.string()
                val error = Gson().fromJson(response, TAG_RESPONSE)
                val sendMessage = error.message
                _isLogin.postValue(error)
                Log.d(TAG_MODEL, "isLogin: $sendMessage")
            }
        }
    }
}