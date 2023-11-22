package com.dicoding.storyapplast.data.preference

data class SharedPrefModel (
    val token: String,
    val userId: String,
    val name: String,
    val isLogin: Boolean = false
)
