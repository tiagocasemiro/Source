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
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.negativeButtonColor
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.dashboard.left.branches.EmptyStateItem
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.Diff
import br.com.source.view.model.FileCommit
import org.eclipse.jgit.diff.DiffEntry
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(close: () -> Unit, rightContainerViewModel: RightContainerViewModel) {
    val hSplitterStateOne = rememberSplitPaneState(0.74f)
    val hSplitterStateTwo = rememberSplitPaneState(0.5f)
    val vSplitterStateOne = rememberSplitPaneState(0.4f)
    var statusToCommit = rightContainerViewModel.listUnCommittedChanges().retryOrNull()
    val stagedFiles = remember { mutableStateOf(statusToCommit?.stagedFiles?: mutableListOf() ) }
    val unStagedFiles = remember { mutableStateOf(statusToCommit?.unStagedFiles?: mutableListOf()) }
    val diff = remember { mutableStateOf<Diff?>(null) }

    val reloadScreen = {
        statusToCommit = rightContainerViewModel.listUnCommittedChanges().retryOrNull()
        stagedFiles.value = statusToCommit?.stagedFiles?: mutableListOf()
        unStagedFiles.value = statusToCommit?.unStagedFiles?: mutableListOf()
    }

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
                            StagedFilesCompose(stagedFiles,
                                onClick = {
                                    rightContainerViewModel.fileDiff(it.name).retryOrNull()?.let { diffFile ->
                                        diff.value = diffFile
                                    }
                                },
                                unStage = {
                                    rightContainerViewModel.removeFileToStageArea(it.name)
                                    reloadScreen()
                                }
                            )
                        }
                        second {
                            UnstagedFilesCompose(unStagedFiles,
                                stage = {
                                    rightContainerViewModel.addFileToStageArea(it.name)
                                    reloadScreen()
                                }
                            )
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
                // call view model to commit
            }
        }
        SourceVerticalSplitter()
    }
}


@Composable
internal fun StagedFilesCompose(stagedFiles: MutableState<MutableList<FileCommit>>, onClick: (file: FileCommit) -> Unit, unStage: (file: FileCommit) -> Unit) {
    val actionRemove: (FileCommit) -> Unit = {
        unStage(it)
    }
    FilesToCommitCompose(stagedFiles, onClick, onDoubleClick = actionRemove, listOf("Remove" to actionRemove))
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UnstagedFilesCompose(unStagedFiles: MutableState<MutableList<FileCommit>>, stage: (FileCommit) -> Unit) {
    val actionAdd: (FileCommit) -> Unit = {
        stage(it)
    }
    FilesToCommitCompose(unStagedFiles, onDoubleClick = actionAdd, items = listOf("Add" to actionAdd))
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun FilesToCommitCompose(files: MutableState<MutableList<FileCommit>>, onClick: (file: FileCommit) -> Unit = {}, onDoubleClick: (file: FileCommit) -> Unit = {}, items: List<Pair<String, (FileCommit) -> Unit>> = emptyList()) {
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
                    files.value.forEach { fileCommit ->
                        val state: ContextMenuState = remember { ContextMenuState() }
                        val menuContext = items.map {
                            ContextMenuItem(it.first) {
                                it.second(fileCommit)
                            }
                        }
                        ContextMenuArea(items = { menuContext } , state = state) {
                            Row(Modifier
                                .height(25.dp)
                                .fillMaxWidth()
                                .detectTapGesturesWithContextMenu(state = state,
                                    onTap = {
                                        onClick(fileCommit)
                                    },
                                    onDoubleTap = {
                                        onDoubleClick(fileCommit)
                                    }
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                val resource = when(fileCommit.changeType) {
                                    DiffEntry.ChangeType.ADD -> {"images/diff/ic-add-file.svg" to "icon modification type add file"}
                                    DiffEntry.ChangeType.COPY -> {"images/diff/ic-copy-file.svg" to "icon modification type copy file"}
                                    DiffEntry.ChangeType.DELETE -> { "images/diff/ic-remove-file.svg" to "icon modification type remove file"}
                                    DiffEntry.ChangeType.MODIFY -> "images/diff/ic-modify-file.svg" to "icon modification type modify file"
                                    DiffEntry.ChangeType.RENAME -> "images/diff/ic-rename-file.svg" to "icon modification type rename file"
                                }
                                Spacer(Modifier.size(10.dp))
                                Icon(
                                    painterResource(resource.first),
                                    contentDescription = resource.second,
                                    modifier = Modifier.size(20.dp)
                                )
                                SourceTooltip(fileCommit.name) {
                                    Text(
                                        text = fileCommit.simpleName(),
                                        modifier = Modifier.padding(start = 10.dp),
                                        fontFamily = Fonts.roboto(),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = itemRepositoryText,
                                        textAlign = TextAlign.Left
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
    EmptyStateItem(diff.value == null) {
        VerticalScrollBox(Modifier.fillMaxSize()) {
            FileDiffCompose(diff.value!!)
        }
    }
}

@Composable
fun MessageContainer(onCancel: () -> Unit, onCommit: (String) -> Unit) {
    val text = remember { mutableStateOf(emptyString()) }
    val scrollState = rememberScrollState()
    Box {
        Column(Modifier.fillMaxSize().padding(10.dp).verticalScroll(scrollState)) {
            SourceTextField(text,
                label = "Message",
                lines = 5
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
                        onCommit(text.value)
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