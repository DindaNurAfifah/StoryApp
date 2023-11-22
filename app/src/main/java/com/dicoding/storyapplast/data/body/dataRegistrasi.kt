package com.dicoding.storyapplast.data.body

import androidx.room.PrimaryKey

data class dataRegistrasi(
    val name: String,

    @PrimaryKey
    val email: String,

    val password: String
)
