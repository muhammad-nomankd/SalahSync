package com.durranitech.salahsync.util

sealed class Resource<out T> {
    data class Success<T>(val data: T): Resource<T>()
    data class Error(val message: String): Resource<Nothing>()
    data class Loading(val loading: Boolean = true): Resource<Nothing>()
}