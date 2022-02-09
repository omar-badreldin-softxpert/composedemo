package com.example.composedemo.network

import okhttp3.Response

class HttpError(
    val response: Response,
    val serverError: ServerError? = null
) : RuntimeException(
    response.let {
        val request = response.request
        "Request: ( url=${request.url}, method=${request.method} ) has failed with response code ${response.code}"
    }
)