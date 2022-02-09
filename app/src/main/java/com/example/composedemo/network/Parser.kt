package com.example.composedemo.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

val gson = Gson()

inline fun <reified T> parse(input: ResponseBody?): T? {
    return input?.string()?.let {
        parseString(it)
    }
}

inline fun <reified T> parseString(input: String?): T? {
    return input?.let {
        gson.fromJson(it, object : TypeToken<T>() {}.type)
    }
}