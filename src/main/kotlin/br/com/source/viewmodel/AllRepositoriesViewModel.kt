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

    fun allRepositories(): List<LocalRepository> {
        return localRepositoryDatabase.all()
    }
}