package br.com.source.model.util

import kotlinx.coroutines.*

class TaskExecutor(private val handleException: HandleException? = null, private var interceptor: Interceptor? = null, private var coroutineScope: CoroutineScope? = null) {
    private var blockException: (Exception) -> Unit = {}

    init {
        onException {
            handleException?.onException(it)
        }
    }

    fun with(coroutineScope: CoroutineScope) {
        this.coroutineScope = coroutineScope
    }

    fun intercept(interceptor: Interceptor) {
        this.interceptor = interceptor
    }

    fun onException(block: (Exception) -> Unit) {
        this.blockException = block
    }

    fun exec(block: suspend () -> Unit) {
        coroutineScope?.launch(Dispatchers.Main) {
            try {
                block()
            } catch (e: Exception) {
                e.printStackTrace()
                blockException(e)
            }
        }
    }

    suspend fun <T>async(block: suspend () -> T?) = coroutineScope?.async(Dispatchers.IO)  {
        try {
            val result = block()

            return@async interceptor?.onIntercept(result)?: result
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                blockException(e)
            }
            return@async null
        }
    }?.await()

    fun stop() {
        coroutineScope?.cancel()
    }
}

interface Interceptor {
    suspend fun <T>onIntercept(result: T) : T
}

interface HandleException {
    fun onException(exception: Exception)
}