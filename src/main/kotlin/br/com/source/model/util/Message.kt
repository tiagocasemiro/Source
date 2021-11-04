package br.com.source.model.util

sealed class Message<T>(val message: String) {
    class Error<T>(msg: String? = null): Message<T>(if(msg.isNullOrEmpty()) generalError() else errorOn(msg))
    class Success<T>(msg: String? = null, val obj: T): Message<T>(msg?: generalSuccess())

    fun isSuccess(): Boolean {
        return this is Success<*>
    }

    fun isError(): Boolean {
        return this is Error<*>
    }

    fun retryOr(replacement: T): T {
        return if (this is Success) obj else replacement
    }

    fun Success<T>.retry(): T {
        return obj
    }

    fun on(error: (Message<T>) -> Unit = {}, success: (T) -> Unit = {}) {
        if(this is Success<T>) {
            success(obj)
        }
        if(isError()) {
            error(this)
        }
    }
}