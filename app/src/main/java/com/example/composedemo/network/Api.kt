package com.example.composedemo.network

import io.reactivex.rxjava3.core.Observable

interface Api<DATA> {

    val url: String

    val method: Method

    fun call(
        url: String = this.url,
        method: Method = this.method,
        queryParams: Set<Param<*>>? = null,
        bodyParams: Any? = null,
        pathParams: Set<Param<*>>? = null,
        headers: Set<Param<*>>?= null
    ): Observable<out Res<DATA>>

}