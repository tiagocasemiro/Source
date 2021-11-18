package br.com.source.view.dashboard.right

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import br.com.source.view.model.Diff
import br.com.source.view.model.Stash
import br.com.source.view.model.StatusToCommit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get

class RightContainerViewModel(localRepository: LocalRepository) {
    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }
    private val coroutine = CoroutineScope(Dispatchers.IO)

    fun stashDiff(stash: Stash, message: (Message<List<Diff>>) -> Unit) {
        coroutine.async {
            val obj = gitService.stashDiff(stash.objectId)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun listUnCommittedChanges(message: (Message<StatusToCommit>) -> Unit) {
        coroutine.async {
            val obj = gitService.unCommittedChanges()
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun addFileToStageArea(fileName: String, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.addFileToStageArea(fileName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun removeFileToStageArea(fileName: String, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.removeFileToStageArea(fileName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun fileDiff(filename: String, message: (Message<Diff>) -> Unit) {
        coroutine.async {
            val obj = gitService.fileDiff(filename)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }

    fun commitFiles(messageCommit: String, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.commitFiles(messageCommit)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }
}