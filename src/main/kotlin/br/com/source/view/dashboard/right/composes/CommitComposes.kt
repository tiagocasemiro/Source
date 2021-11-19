package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.detectTapGesturesWithContextMenu
import br.com.source.model.util.emptyString
import br.com.source.model.util.emptyValidation
import br.com.source.model.util.validation
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.negativeButtonColor
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.components.TypeCommunication
import br.com.source.view.components.showDialogSingleButton
import br.com.source.view.dashboard.left.branches.EmptyStateItem
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.Diff
import br.com.source.view.model.FileCommit
import org.eclipse.jgit.diff.DiffEntry.ChangeType.*
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(close: () -> Unit, rightContainerViewModel: RightContainerViewModel) {
    val hSplitterStateOne = rememberSplitPaneState(0.74f)
    val hSplitterStateTwo = rememberSplitPaneState(0.5f)
    val vSplitterStateOne = rememberSplitPaneState(0.4f)
    val diff = remember { mutableStateOf<Diff?>(null) }
    val stagedFiles = remember { mutableStateOf(mutableListOf<FileCommit>() ) }
    val unStagedFiles = remember { mutableStateOf(mutableListOf<FileCommit>() ) }
    val hasConflict = remember { mutableStateOf(false) }
    val updateStatusToCommit = {
        rightContainerViewModel.listUnCommittedChanges { message ->
            message.onSuccessWithDefaultError { statusToCommit ->
                hasConflict.value = statusToCommit.stagedFiles.any { it.isConflict }
                stagedFiles.value = statusToCommit.stagedFiles
                unStagedFiles.value = statusToCommit.unStagedFiles
            }
        }
    }

    if(hasConflict.value) {
        showNotification("Conflict detected, resolve before commit", TypeCommunication.warn)
    }

    val actionDiffFile = remember { mutableStateOf<FileCommit?>(null) }
    val actionUnStagFile = remember { mutableStateOf<FileCommit?>(null) }
    val actionStageFile = remember { mutableStateOf<FileCommit?>(null) }
    val actionRevertFile = remember { mutableStateOf<FileCommit?>(null) }

    if(actionRevertFile.value != null) {
        showLoad()
        rightContainerViewModel.revertFile(actionRevertFile.value!!.name) { message ->
            message.onSuccessWithDefaultError {
                updateStatusToCommit()
                diff.value = null
                hideLoad()
            }
            actionRevertFile.value = null
        }
    }

    if(actionDiffFile.value != null) {
        showLoad()
        rightContainerViewModel.fileDiff(actionDiffFile.value!!.name) { message ->
            message.onSuccessWithDefaultError { diffFile ->
                diff.value = diffFile
                hideLoad()
            }
            actionDiffFile.value = null
        }
    }

    if(actionUnStagFile.value != null) {
        showLoad()
        rightContainerViewModel.removeFileToStageArea(actionUnStagFile.value!!.name) { message ->
            message.onSuccessWithDefaultError {
                updateStatusToCommit()
                hideLoad()
            }
            actionUnStagFile.value = null
        }
    }

    if(actionStageFile.value != null) {
        showLoad()
        rightContainerViewModel.addFileToStageArea(actionStageFile.value!!.name) { message ->
            message.onSuccessWithDefaultError {
                updateStatusToCommit()
                hideLoad()
            }
            actionStageFile.value = null
        }
    }

    updateStatusToCommit()
    VerticalSplitPane(
        splitPaneState = hSplitterStateOne,
        modifier = Modifier.background(StatusStyle.backgroundColor)
    ) {
        first {
            HorizontalSplitPane(
                splitPaneState = vSplitterStateOne,
                modifier = Modifier.background(StatusStyle.backgroundColor)
            ) {
                first {
                    VerticalSplitPane(
                        splitPaneState = hSplitterStateTwo,
                        modifier = Modifier.background(StatusStyle.backgroundColor)
                    ) {
                        first {
                            StagedFilesCompose(stagedFiles, actionDiffFile, actionUnStagFile, actionRevertFile)
                        }
                        second {
                            UnstagedFilesCompose(unStagedFiles, actionStageFile)
                        }
                        SourceVerticalSplitter()
                    }
                }
                second {
                    DiffFileCompose(diff)
                }
                SourceHorizontalSplitter()
            }
        }
        second{
            MessageContainer(close) {
                if(hasConflict.value) {
                    showDialogSingleButton("Conflict detected","Resolve this conflict before commit. \nUse a external tools to make merge", TypeCommunication.warn)
                } else {
                    showLoad()
                    rightContainerViewModel.commitFiles(it) { message ->
                        message.onSuccessWithDefaultError {
                            close()
                        }
                        hideLoad()
                    }
                }
            }
        }
        SourceVerticalSplitter()
    }
}


@Composable
internal fun StagedFilesCompose(stagedFiles: MutableState<MutableList<FileCommit>>, onClick: MutableState<FileCommit?>, unStage: MutableState<FileCommit?>, revert: MutableState<FileCommit?>) {
    FilesToCommitCompose("Staged files", stagedFiles, onClick = onClick, onDoubleClick = unStage, listOf("Remove" to unStage, "Revert" to revert))
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UnstagedFilesCompose(unStagedFiles: MutableState<MutableList<FileCommit>>, stage: MutableState<FileCommit?>) {
    FilesToCommitCompose("Unstaged files", files = unStagedFiles, onDoubleClick = stage, items = listOf("Add" to stage))
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FilesToCommitCompose(title: String, files: MutableState<MutableList<FileCommit>>, onClick: MutableState<FileCommit?> = mutableStateOf(null), onDoubleClick: MutableState<FileCommit?>, items: List<Pair<String, MutableState<FileCommit?>>> = emptyList()) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
            contentAlignment = Alignment.CenterStart) {
            Text( title,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
        }
        HorizontalDivider()
        EmptyStateItem(files.value.isEmpty()) {
            Box {
                VerticalScrollBox {
                    Column(Modifier.fillMaxSize()) {
                        files.value.forEachIndexed { index, _ ->
                            val color = if(index % 2 == 1) Color.Transparent else cardBackgroundColor
                            Spacer(Modifier.height(25.dp).fillMaxWidth().background(color))
                        }
                    }
                }
                FullScrollBox(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize()) {
                        files.value.forEachIndexed { index, _ ->
                            val fileCommit = files.value[index]
                            Row(Modifier
                                .height(25.dp)
                                .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val resource = when(fileCommit.changeType) {
                                    ADD -> { "images/diff/ic-add-file.svg" to "icon modification type add file" }
                                    COPY -> { "images/diff/ic-copy-file.svg" to "icon modification type copy file" }
                                    DELETE -> { "images/diff/ic-remove-file.svg" to "icon modification type remove file" }
                                    MODIFY -> { "images/diff/ic-modify-file.svg" to "icon modification type modify file" }
                                    RENAME -> { "images/diff/ic-rename-file.svg" to "icon modification type rename file" }
                                }
                                val resourceConflict = "images/diff/ic-conflict-file.svg" to "icon modification type conflict file"
                                Spacer(Modifier.size(10.dp))
                                Icon(
                                    painterResource(if(fileCommit.isConflict) resourceConflict.first else resource.first),
                                    contentDescription = resource.second,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = fileCommit.simpleName(),
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontFamily = Fonts.roboto(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = itemRepositoryText,
                                    textAlign = TextAlign.Left
                                )
                            }
                        }
                    }
                }
                VerticalScrollBox {
                    Column(Modifier.fillMaxSize()) {
                        files.value.forEachIndexed { index, _ ->
                            val state: ContextMenuState = remember { ContextMenuState() }
                            val menuContext = items.map {
                                ContextMenuItem(it.first) {
                                    it.second.value = files.value[index]
                                }
                            }
                            ContextMenuArea(items = { menuContext } , state = state) {
                                SourceTooltip(files.value[index].name) {
                                    Spacer(Modifier
                                        .height(25.dp)
                                        .fillMaxWidth()
                                        .detectTapGesturesWithContextMenu(state = state,
                                            onTap = {
                                                onClick.value = files.value[index]
                                            },
                                            onDoubleTap = {
                                                onDoubleClick.value = files.value[index]
                                            }
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun DiffFileCompose(diff: MutableState<Diff?>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "Diff file",
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
        }
        HorizontalDivider()
        EmptyStateItem(diff.value == null) {
            VerticalScrollBox(Modifier.fillMaxSize()) {
                FileDiffCompose(diff.value!!)
            }
        }
    }
}

@Composable
fun MessageContainer(onCancel: () -> Unit, onCommit: (String) -> Unit) {
    val text = remember { mutableStateOf(emptyString()) }
    val textValidation = remember { mutableStateOf(emptyString()) }

    val scrollState = rememberScrollState()
    Box {
        Column(Modifier.fillMaxSize().padding(10.dp).verticalScroll(scrollState)) {
            SourceTextField(text,
                label = "Message",
                lines = 5,
                errorMessage = textValidation
            )
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement =  Arrangement.End,
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth()
            ) {
                SourceButton("cancel", color = negativeButtonColor) {
                    onCancel()
                }
                Spacer(modifier = Modifier.width(10.dp))
                SourceTooltip("Commit changes on stage") {
                    SourceButton("Commit") {
                        if(text.validation(listOf(emptyValidation("Message to commit  is required")), textValidation)) {
                            onCommit(text.value)
                        }
                    }
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }
}