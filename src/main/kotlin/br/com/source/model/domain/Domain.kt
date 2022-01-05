package br.com.source.model.domain

import br.com.source.model.util.emptyString
import org.dizitart.no2.objects.Id
import java.io.File

data class LocalRepository(
    var name: String = emptyString(),
    @Id
    var workDir: String = emptyString(),
    var credentialType: String = CredentialType.HTTP.value
) {
    // http
    var username: String = emptyString()
    var password: String = emptyString()
    // ssh
    var pathKey: String = emptyString()
    var passwordKey: String = emptyString()
    var host: String = emptyString()

    fun fileWorkDir(): File {
        val file = File(workDir)
        if(file.exists().not()) {
            file.mkdir()
        }

        return file
    }
}
data class RemoteRepository(val url: String, val localRepository: LocalRepository)

enum class CredentialType(val value: String) {
    HTTP("http"), SSH("ssh")
}