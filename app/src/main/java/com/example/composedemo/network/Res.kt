package com.example.composedemo.network

import okhttp3.Response

open class Res<DATA> {

    val data: DATA?
    val rawResponse: Response?
    val error: Throwable?

    constructor(data: DATA? = null, rawResponse: Response? = null, error: Throwable? = null) {
        this.data = data
        this.rawResponse = rawResponse
        this.error = error
    }

    constructor(res: Res<DATA>): this(res.data, res.rawResponse, res.error)

    val isSuccessful
        get() = rawResponse?.isSuccessful ?: false
}