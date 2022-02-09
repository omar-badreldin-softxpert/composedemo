package com.example.composedemo.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ServerError(
    @field:Expose
    @field:SerializedName("error")
    val error: String? = null
)
