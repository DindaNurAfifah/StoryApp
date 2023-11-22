package com.dicoding.storyapplast.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapplast.data.StoriesRepository
import com.dicoding.storyapplast.data.body.dataRegistrasi
import com.dicoding.storyapplast.data.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegistrasiViewModel(private val repository: StoriesRepository): ViewModel() {

    companion object{
        private val TAG_MODEL = RegistrasiViewModel::class.java.simpleName
        private val TAG_RESPONSE = RegisterResponse::class.java
    }

    private val _isRegister = MutableLiveData<RegisterResponse>()
    val isRegister: LiveData<RegisterResponse> = _isRegister

    fun getRegister(data: dataRegistrasi) {
        viewModelScope.launch {
            try {
                val response = repository.getRegister(data)
                _isRegister.postValue(response)
            } catch (e: HttpException) {
                val response = e.response()?.errorBody()?.string()
                val error = Gson().fromJson(response, TAG_RESPONSE)
                val sendMessage = error.message
                _isRegister.postValue(error)
                Log.d(TAG_MODEL, "isLogin: $sendMessage")
            }
        }
    }
}