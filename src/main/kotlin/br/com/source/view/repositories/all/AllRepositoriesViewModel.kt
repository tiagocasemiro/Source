package br.com.source.view.repositories.all

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.domain.LocalRepository
import br.com.source.model.process.runCommand
import br.com.source.model.service.GitService
import br.com.source.model.util.TaskExecutor
import br.com.source.model.util.emptyString
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AllRepositoriesViewModel(private val localRepositoryDatabase: LocalRepositoryDatabase) {
    private val _repositories = MutableStateFlow<List<LocalRepository>>(emptyList())
    val repositories: StateFlow<List<LocalRepository>> = _repositories
    private val _status = MutableStateFlow(emptyString())
    val status: StateFlow<String> = _status
    private val task = TaskExecutor(coroutineScope = CoroutineScope(Dispatchers.IO))

    init {
        all()
    }

    fun checkRepository(localRepository: LocalRepository, onOk: () -> Unit) {
        task.exec {
            val result = task.async { GitService(localRepository).checkRepository() }
            result?.onSuccess {
                onOk()
            }
        }
    }

    fun status(workDir: String) {
        task.exec {
            task.async { runCommand("git status", File(workDir)) }?.let {
                _status.value = it
            }
        }
    }

    fun all() {
        task.exec {
            task.async { localRepositoryDatabase.all() }?.let {
                _repositories.value = it
            }
            _repositories.value.firstOrNull()?.let {
                task.async { GitService(it).checkRepository() }
            }
        }
    }

    fun delete(localRepository: LocalRepository) {
         task.exec {
             task.async { localRepositoryDatabase.delete(localRepository) }
             task.async { localRepositoryDatabase.all() }?.let {
                 _repositories.value = it
             }
            _status.value = emptyString()
        }
    }
}