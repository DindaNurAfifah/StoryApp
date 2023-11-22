package com.dicoding.storyapplast.data.body

import androidx.room.PrimaryKey

data class dataLogin (
    @PrimaryKey
    val email: String,

    val password: String
)
