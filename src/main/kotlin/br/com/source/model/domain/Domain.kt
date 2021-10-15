package br.com.source.model.domain

import java.io.File

data class LocalRepository(
    val workDir: String,
    val credential: Credential
    ) {
    fun workDir(): File {
        return File(workDir)
    }
}
data class RemoteRepository(val url: String, val localRepository: LocalRepository)
data class Credential(val login: String, val password: String)