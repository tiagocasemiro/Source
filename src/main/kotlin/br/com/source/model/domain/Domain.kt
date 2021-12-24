package br.com.source.model.domain

import br.com.source.model.util.emptyString
import org.dizitart.no2.objects.Id
import java.io.File

data class LocalRepository(
    var name: String = "",
    @Id
    var workDir: String = "",
    var credential: Credential = Credential.Http()
    ) {
    fun fileWorkDir(): File {
        val file = File(workDir)
        if(file.exists().not()) {
            file.mkdir()
        }

        return file
    }
}
data class RemoteRepository(val url: String, val localRepository: LocalRepository)
open class Credential {
    data class Http(var username: String = emptyString(), var password: String = emptyString()): Credential()
    data class Ssh(var key: String = emptyString(), var password: String = emptyString(), var host: String = emptyString()): Credential()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}