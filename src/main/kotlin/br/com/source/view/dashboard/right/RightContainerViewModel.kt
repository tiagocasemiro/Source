package br.com.source.view.dashboard.right

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.model.util.Message
import br.com.source.view.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get

class RightContainerViewModel(localRepository: LocalRepository) {
    private val gitService: GitService = get(GitService::class.java) { parametersOf(localRepository.fileWorkDir()) }
    private val coroutine = CoroutineScope(Dispatchers.IO)
    private val _commits = MutableStateFlow<Message<List<CommitItem>>>(Message.Success(obj = emptyList()) )
    val commits: StateFlow<Message<List<CommitItem>>> = _commits
    private val _filesFromCommit = MutableStateFlow<Message<CommitDetail>>(Message.Success(obj = CommitDetail()) )
    val filesFromCommit: StateFlow<Message<CommitDetail>> = _filesFromCommit
    private val _diff = MutableStateFlow<Message<Diff?>>(Message.Success(obj = null) )
    val diff: StateFlow<Message<Diff?>> = _diff
    private val _showLoad = MutableStateFlow(false)
    val showLoad: StateFlow<Boolean> = _showLoad

    fun history() {
        _showLoad.value = true
        coroutine.async {
            val commits = gitService.history()
            _commits.value = commits
            val commit = commits.retryOrNull()?.firstOrNull()
            if(commit != null) {
                val filesFromCommit = gitService.filesChangesOn(commit.hash).retryOr(emptyList())
                _filesFromCommit.value = Message.Success(obj = CommitDetail(filesFromCommit = filesFromCommit, resume = commit.resume()))
                filesFromCommit.firstOrNull()?.let {
                    selectFileFromCommit(it)
                }
            }
            _showLoad.value = false
        }.start()
    }

    fun selectCommit(commit: CommitItem) {
        coroutine.async {
            when(val it = gitService.filesChangesOn(commit.hash)) {
                is Message.Success -> {
                    val filesFromCommit = it.retryOr(emptyList())
                    if(filesFromCommit.isEmpty()) {
                        _diff.value = Message.Success(obj = null)
                    } else {
                        filesFromCommit.firstOrNull()?.let {
                            selectFileFromCommit(it)
                        }
                    }
                    _filesFromCommit.value = Message.Success(obj = CommitDetail(filesFromCommit, commit.resume()))
                }
                is Message.Error -> {
                    _filesFromCommit.value = Message.Error(it.message)
                }
                is Message.Warn -> {
                    _filesFromCommit.value = Message.Warn(it.message)
                }
            }
        }.start()
    }

    fun selectFileFromCommit(file: FileCommit) {
        coroutine.async {
           val returnedMessage = gitService.fileDiffOn(file.hash!!, file.name)
            _diff.value = Message.Success(obj = returnedMessage.retryOrNull(), msg = returnedMessage.message)
        }.start()
    }

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

    fun revertFile(fileName: String, message: (Message<Unit>) -> Unit) {
        coroutine.async {
            val obj = gitService.revertFile(fileName)
            withContext(Dispatchers.Main) {
                message(obj)
            }
        }.start()
    }
}