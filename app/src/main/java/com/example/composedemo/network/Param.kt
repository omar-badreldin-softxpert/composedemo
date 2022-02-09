package com.example.composedemo.network

open class Param<T>(
    val key: String,
    val value: T
) {

    val valueString
        get() = value.toString()

    override fun toString() = "$key=$valueString"
}