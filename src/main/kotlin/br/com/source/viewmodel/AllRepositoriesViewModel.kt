package br.com.source.viewmodel

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.git.Executor
import br.com.source.model.process.runCommand
import java.io.File

class AllRepositoriesViewModel(private val executor: Executor, private val localRepositoryDatabase: LocalRepositoryDatabase) {

    fun status(workDir: String): String {
        return runCommand("git status", File(workDir))
    }

    fun all(): List<LocalRepository> {
        return localRepositoryDatabase.all()
    }

    fun delete(localRepository: LocalRepository) {
        localRepositoryDatabase.delete(localRepository)
    }
}