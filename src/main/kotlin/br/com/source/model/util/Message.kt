package br.com.source.model.util

import br.com.source.view.common.hideLoad
import br.com.source.view.components.showActionError

sealed class Message<T>(val message: String) {
    class Warn<T>(msg: String): Message<T>(msg)
    class Error<T>(msg: String? = null): Message<T>(if(msg.isNullOrEmpty()) generalError() else errorOn(msg))
    class Success<T>(msg: String? = null, val obj: T): Message<T>(msg?: generalSuccess())

    fun isSuccess(): Boolean {
        return this is Success<*>
    }

    fun isError(): Boolean {
        return this is Error<*>
    }

    fun isWarn(): Boolean {
        return this is Warn<*>
    }

    fun retryOr(replacement: T): T {
        return if (this is Success) obj else replacement
    }

    fun on(error: (Message<T>) -> Unit = {}, success: (T) -> Unit = {}, warn: (Message<T>) -> Unit = {}) {
        if(this is Success<T>) {
            success(obj)
        }
        if(isError()) {
            error(this)
        }
        if(isWarn()) {
            warn(this)
        }
    }

    fun onSuccessWithDefaultError(success: (T) -> Unit) {
        on(error = {
            showActionError(it)
            hideLoad()
        },
        success = success)
    }

    fun onSuccessWithWarnDefaultError(warn: (Message<T>) -> Unit = {}, success: (T) -> Unit) {
        on(error = {
            showActionError(it)
            hideLoad()
        },
        success = success,
        warn = warn)
    }
}