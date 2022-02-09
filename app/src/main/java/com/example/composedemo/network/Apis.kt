package com.example.composedemo.network

import okhttp3.OkHttpClient

inline fun <reified T> api(
    url: String? = null,
): Api<T> {

    return object : BaseApi<T>(
        okHttpClient = OkHttpClient(),
        parser = ::parse,
    ) {
        override val url: String
            get() = url ?: throw IllegalStateException()
    }
}

const val BASE_URL = "https://jsonplaceholder.typicode.com/"
const val URL_PHOTOS = "${BASE_URL}photos"