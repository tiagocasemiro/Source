package br.com.source.model.util

sealed class Message<T>(val message: String) {
    class Error<T>(msg: String? = null): Message<T>(if(msg.isNullOrEmpty()) generalError() else errorOn(msg))
    class Success<T>(msg: String? = null, val obj: T? = null): Message<T>(msg?: generalSuccess())

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