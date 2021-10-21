package br.com.source.model.domain

import java.io.File

data class LocalRepository(
    var name: String = "",
    var workDir: String = "",
    var credential: Credential = Credential()
    ) {
    fun workDir(): File {
        return File(workDir)
    }
}
data class RemoteRepository(val url: String, val localRepository: LocalRepository)
data class Credential(var username: String = "", var password: String = "")