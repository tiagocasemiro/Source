package br.com.source.model.domain

import org.dizitart.no2.objects.Id
import java.io.File

data class LocalRepository(
    var name: String = "",
    @Id
    var workDir: String = "",
    var credential: Credential = Credential()
    ) {
    fun fileWorkDir(): File {
        return File(workDir)
    }
}
data class RemoteRepository(val url: String, val localRepository: LocalRepository)
data class Credential(var username: String = "", var password: String = "")