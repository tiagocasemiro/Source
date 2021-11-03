package br.com.source.model.util

sealed class Message<T>(val message: String? = null) {
    class Error<T>(private val msg: String? = null): Message<T>(msg)
    class Success<T>(private val msg: String? = null, val obj: T? = null): Message<T>(msg)

    fun isSuccess(): Boolean {
        return this is Success<*>
    }

    fun isError(): Boolean {
        return this is Error<*>
    }

    fun retryOr(replacement: T): T {
        return if (this is Success) obj?: replacement else replacement
    }
}