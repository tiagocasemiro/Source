package br.com.source.model.domain

import java.io.File

data class LocalRepository(
    val name: String,
    val workDir: String,
    val credential: Credential
    ) {
    fun workDir(): File {
        return File(workDir)
    }
}
data class RemoteRepository(val url: String, val localRepository: LocalRepository)
data class Credential(val username: String, val password: String)