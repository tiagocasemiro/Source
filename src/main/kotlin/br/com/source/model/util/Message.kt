package br.com.source.model.util

sealed class Message {
    class Error(val message: String): Message()
    class Success(val message: String? = null): Message()

    fun isSuccess(): Boolean {
        return this is Success
    }
}