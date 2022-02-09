package com.example.composedemo.network

import com.example.composedemo.network.Method.*
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import okio.IOException

abstract class BaseApi<DATA>(
    val okHttpClient: OkHttpClient,
    val parser: (ResponseBody?) -> DATA?,
    val errorParser: (ResponseBody?) -> ServerError? = ::parse
) : Api<DATA> {

    companion object {
        @JvmStatic
        private val gson = Gson()

        @JvmStatic
        private val jsonMediaType: MediaType = "application/json; charset=utf-8".toMediaType()
    }

    override val method: Method = GET

    var throwError: Boolean = true

    override fun call(
        url: String,
        method: Method,
        queryParams: Set<Param<*>>?,
        bodyParams: Any?,
        pathParams: Set<Param<*>>?,
        headers: Set<Param<*>>?
    ): Observable<out Res<DATA>> {
        return Observable.create<Res<DATA>> { emitter ->
            val request = Request.Builder()
                /**
                 * Setting url
                 */
                .let { builder ->
                    builder.url(
                        url
                            /**
                             * Setting path params
                             */
                            .let {
                                if (pathParams.isNullOrEmpty()) it
                                else {
                                    var urlWithPathParamsSet = it
                                    pathParams.forEach { param ->
                                        urlWithPathParamsSet = urlWithPathParamsSet.replace(
                                            "{${param.key}}", param.valueString
                                        )
                                    }
                                    urlWithPathParamsSet
                                }
                            }
                            /**
                             * Setting query params
                             */
                            .let {
                                if (queryParams.isNullOrEmpty()) it
                                else queryParams
                                    .joinToString("&") { param -> param.toString() }
                                    .let { joinedParams -> "$it?$joinedParams" }
                            }
                    )
                }
                /**
                 * Setting method
                 */
                .let { builder ->
                    val body: RequestBody? = if (method == GET) null
                    else {
                        bodyParams?.let {
                            gson.toJson(it).toRequestBody(
                                jsonMediaType
                            )
                        }
                    }
                    when (method) {
                        GET -> builder.get()
                        POST -> builder.post(body ?: EMPTY_REQUEST)
                        PUT -> builder.put(body ?: EMPTY_REQUEST)
                        DELETE -> builder.delete(body ?: EMPTY_REQUEST)
                        PATCH -> builder.patch(body ?: EMPTY_REQUEST)
                    }
                }
                /**
                 * Setting headers
                 */
                .let { builder ->
                    if (headers == null || headers.isEmpty()) builder
                    else builder.headers(
                        Headers.Builder()
                            .also {
                                headers.forEach { param ->
                                    it.add(param.key, param.valueString)
                                }
                            }.build()
                    )
                }.build()
            val call = okHttpClient.newCall(request)
            emitter.setCancellable { call.cancel() }
            val result = call.runCatching { execute() }
            val res: Res<DATA>? = if (result.isSuccess) {
                val response = result.getOrThrow()
                if (response.isSuccessful) {
                    try {
                        Res(
                            data = parser.invoke(response.body),
                            rawResponse = response
                        )
                    } catch (e: Throwable) {
                        Res(
                            rawResponse = response,
                            error = e
                        )
                    }
                } else Res(
                    rawResponse = response,
                    error = HttpError(
                        response,
                        serverError = response.body.runCatching {
                            errorParser.invoke(this)
                        }.getOrNull()
                    )
                )
            } else {
                val error = result.exceptionOrNull()
                if (error is IOException) null
                else Res(error = error)
            }
            res?.apply { emitter.onNext(this) }
            emitter.onComplete()
        }.flatMap {
            if (throwError && it.error != null) Observable.error<Res<DATA>>(it.error)
            else Observable.just(it)
        }
    }
}