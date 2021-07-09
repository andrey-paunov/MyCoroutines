package com.example.mycoroutines

import java.lang.Exception

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Undefined<out K>(val data: K) : Result<K>()
}