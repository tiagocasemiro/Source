package br.com.source.model.process

import br.com.source.model.util.emptyString
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun runCommand(command: String, directory: File): String {
    return try {
        val parts = command.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(directory)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()

        proc.waitFor(60, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        emptyString()
    }
}