package com.dicoding.storyapplast.data.response

import com.google.gson.annotations.SerializedName

data class UpStoriesResponse(

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null

)