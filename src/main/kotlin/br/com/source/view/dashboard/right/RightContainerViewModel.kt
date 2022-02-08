package br.com.source.view.dashboard.right

import br.com.source.model.domain.LocalRepository
import br.com.source.model.service.GitService
import br.com.source.view.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RightContainerViewModel(localRepository: LocalRepository) {
    private val gitService: GitService = GitService(localRepository)
    private val coroutine = CoroutineScope(Dispatchers.Main)
    private val _commits = MutableStateFlow<List<CommitItem>>(emptyList())
    val commits: StateFlow<List<CommitItem>> = _commits
    private val _filesFromCommit = MutableStateFlow<CommitDetail?>(null)
    val filesFromCommit: StateFlow<CommitDetail?> = _filesFromCommit
    private val _diff = MutableStateFlow<Diff?>(null)
    val diff: StateFlow<Diff?> = _diff
    private val _showLoad = MutableStateFlow(false)
    val showLoad: StateFlow<Boolean> = _showLoad
    var onConflictDetected: () -> Unit = {}
    private val _selectedIndex = MutableStateFlow(-1)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    fun history(branch: Branch? = null) {
        _showLoad.value = true
        coroutine.async {
            gitService.history(branch).onSuccess { commits ->
                _commits.value = processLog(commits)
                commits.firstOrNull()?.let { _ ->
                    selectCommit(0)
                }
            }
            _showLoad.value = false
        }.start()
    }
    fun selectCommit(indexCommit: Int) {
        if(_selectedIndex.value == indexCommit) {
            return
        }
        _showLoad.value = true
        coroutine.async {
            _selectedIndex.value = indexCommit
            val commit: CommitItem = commits.value[indexCommit]
            gitService.filesChangesOn(commit.hash).onSuccess { filesFromCommit ->
                if(filesFromCommit.isEmpty()) {
                    _diff.value = null
                } else {
                    filesFromCommit.firstOrNull()?.let {
                        selectFileFromCommit(it)
                    }
                }
                _filesFromCommit.value = CommitDetail(
                    filesFromCommit,
                    commit.hash,
                    commit.author,
                    commit.date,
                    commit.fullMessage,
                    commit.node.branches.map { it.fullName },
                    commit.node.tags.map { it.name }
                )
            }
            _showLoad.value = false
        }.start()
    }

    fun selectFileFromCommit(file: FileCommit) {
        _showLoad.value = true
        coroutine.async {
            gitService.fileDiffOn(file.hash!!, file.name).onSuccess {
                _diff.value = it
            }
            _showLoad.value = false
        }.start()
    }

    private val _commitDiff = MutableStateFlow<Diff?>(null)
    val commitDiff: StateFlow<Diff?> = _commitDiff
    private val _statusToCommit = MutableStateFlow<StatusToCommit?>(null)
    val statusToCommit: StateFlow<StatusToCommit?> = _statusToCommit

    fun listUnCommittedChanges() {
        _showLoad.value = true
        coroutine.async {
            gitService.unCommittedChanges().onSuccess {
                if(it.hasConflict()) {
                    onConflictDetected()
                }
                _statusToCommit.value = it
            }
            _showLoad.value = false
        }.start()
    }

    fun revertFile(fileName: String) {
        _showLoad.value = true
        coroutine.async {
            gitService.revertFile(fileName).onSuccess {
                _commitDiff.value = null
                listUnCommittedChanges()
            }
            _showLoad.value = false
        }.start()
    }

    fun fileDiff(filename: String) {
        _showLoad.value = true
        coroutine.async {
            gitService.fileDiff(filename).onSuccess {
                _commitDiff.value = it
            }
            _showLoad.value = false
        }.start()
    }

    fun removeFileToStageArea(fileName: String) {
        _showLoad.value = true
        coroutine.async {
            gitService.removeFileToStageArea(fileName).onSuccess {
                listUnCommittedChanges()
            }
            _showLoad.value = false
        }.start()
    }

    fun addFileToStageArea(fileName: String) {
        _showLoad.value = true
        coroutine.async {
            gitService.addFileToStageArea(fileName).onSuccess {
                listUnCommittedChanges()
            }
            _showLoad.value = false
        }.start()
    }

    fun commitFiles(messageCommit: String, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.commitFiles(messageCommit).onSuccess {
                history()
                onSuccess()
            }
            _showLoad.value = false
        }.start()
    }

    private val _stashDiff = MutableStateFlow<List<Diff>>(emptyList())
    val stashDiff: StateFlow<List<Diff>> = _stashDiff

    fun stashDiff(stash: Stash) {
        _showLoad.value = true
        coroutine.async {
            gitService.stashDiff(stash.objectId).onSuccess {
                _stashDiff.value = it
            }
            _showLoad.value = false
        }.start()
    }

    private fun processLog(commits: List<CommitItem>): List<CommitItem> {
        commits.forEachIndexed { index, commit ->
            val currentNode = commit.node
            val nextNode = if(index + 1 < commits.size) commits[index + 1].node else null
            val beforeNode = if(index > 0) commits[index - 1].node else null
            var lineGraph = mutableListOf<Draw>()
            if(index == 0 && commits.size > 1) {
                commits[1].node.line.forEachIndexed {  indexNextItemLine, nextItemLine ->
                    lineGraph.add(Draw.Line(start = Point(0, Position.MEDDLE), end = Point(indexNextItemLine, Position.BOTTOM), color = retryColor(nextItemLine!!.color)))
                }
                lineGraph.add(Draw.Commit(0, retryColor(commits[1].node.line[0]!!.color)))
                commit.drawLine = lineGraph

                return@forEachIndexed
            }
            lineGraph = mutableListOf()
            currentNode.line.forEachIndexed { indexCurrentItemLine, currentItemLine ->
                if(currentItemLine?.hash == currentNode.hash) {
                    var nextColor: Int? = null
                    var nextColorPriority: Int? = null
                    var beforeIsDraw = false
                    if(beforeNode != null && beforeNode.line.isNotEmpty() && beforeNode.line.size > indexCurrentItemLine) {
                        beforeNode.line.forEachIndexed { indexBeforeItemLine, beforeItemLine ->
                            if(beforeItemLine == currentItemLine) {
                                beforeIsDraw = true
                                lineGraph.add(Draw.Line(start = Point(indexBeforeItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), retryColor(currentItemLine.color)))
                            }
                        }
                    }
                    if(beforeNode?.parents?.contains(currentItemLine.hash) == true) {
                        beforeIsDraw = true
                        lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), retryColor(currentItemLine.color)))
                    }
                    if(index == 1 || currentNode.parents.isEmpty()) {
                        lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), retryColor(currentItemLine.color)))
                    }
                    currentNode.parents.forEach{ parent ->
                        nextNode?.line?.forEachIndexed { indexNextItemLine, nextItemLine ->
                            if (nextItemLine?.hash == parent) {
                                nextColor = nextItemLine.color
                                if(indexNextItemLine == indexCurrentItemLine) {
                                    nextColorPriority = nextItemLine.color
                                }
                                lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.MEDDLE), end = Point(indexNextItemLine, Position.BOTTOM), retryColor(nextItemLine.color)))
                                if(nextItemLine.hash == nextNode.hash) {
                                    return@forEach
                                }
                            }
                        }
                    }
                    lineGraph.add(Draw.Commit(indexCurrentItemLine, if(beforeIsDraw) retryColor(currentItemLine.color) else retryColor(nextColorPriority?: nextColor?: currentItemLine.color)))
                } else {
                    if(beforeNode != null && beforeNode.line.isNotEmpty()) {
                        beforeNode.line.forEachIndexed { indexBeforeItemLine, beforeItemLine ->
                            if(beforeItemLine == currentItemLine && currentItemLine != null) {
                                lineGraph.add(Draw.Line(start = Point(indexBeforeItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.MEDDLE), retryColor(currentItemLine.color)))
                                lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.MEDDLE), end = Point(indexCurrentItemLine, Position.BOTTOM), retryColor(currentItemLine.color)))
                            } else if(beforeNode.parents.contains(currentItemLine?.hash) && beforeItemLine?.hash == beforeNode.hash) {
                                lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.BOTTOM), retryColor(currentItemLine!!.color)))
                            }
                        }
                    }
                    if(index == 1) {
                        lineGraph.add(Draw.Line(start = Point(indexCurrentItemLine, Position.TOP), end = Point(indexCurrentItemLine, Position.BOTTOM), retryColor(currentItemLine!!.color)))
                    }
                }
            }
            commit.drawLine = lineGraph
        }

        return commits
    }

    fun createTag(name: String, hashCommit: String, onSuccess: () -> Unit) {
        _showLoad.value = true
        coroutine.async {
            gitService.createTag(name, hashCommit).onSuccess {
               history()
               onSuccess()
            }
            _showLoad.value = false
        }.start()
    }
}