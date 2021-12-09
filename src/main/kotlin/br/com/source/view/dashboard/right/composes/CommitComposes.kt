package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.emptyString
import br.com.source.model.util.emptyValidation
import br.com.source.model.util.validation
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.negativeButtonColor
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.dashboard.left.branches.EmptyStateOnNullItem
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.Diff
import br.com.source.view.model.FileCommit
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(close: () -> Unit, rightContainerViewModel: RightContainerViewModel) {
    val hSplitterStateOne = rememberSplitPaneState(0.74f)
    val hSplitterStateTwo = rememberSplitPaneState(0.5f)
    val vSplitterStateOne = rememberSplitPaneState(0.4f)
    val showLoad = rightContainerViewModel.showLoad.collectAsState()
    val diff = rightContainerViewModel.commitDiff.collectAsState()
    val statusToCommit = rightContainerViewModel.statusToCommit.collectAsState()
    val actionRevertFile: (FileCommit) -> Unit = { rightContainerViewModel.revertFile(it.name) }
    val actionDiffFile: (FileCommit) -> Unit = { rightContainerViewModel.fileDiff(it.name) }
    val actionUnStagFile: (FileCommit) -> Unit = { rightContainerViewModel.removeFileToStageArea(it.name) }
    val actionStageFile: (FileCommit) -> Unit = { rightContainerViewModel.addFileToStageArea(it.name) }
    rightContainerViewModel.onConflictDetected = { showWarnNotification("Conflict detected, resolve before commit") }
    rightContainerViewModel.listUnCommittedChanges()

    LoadState(showLoad) {
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
                                StagedFilesCompose(statusToCommit.value?.stagedFiles ?: emptyList(), actionDiffFile, actionUnStagFile, actionRevertFile)
                            }
                            second {
                                UnstagedFilesCompose(statusToCommit.value?.unStagedFiles ?: emptyList(), actionStageFile)
                            }
                            SourceVerticalSplitter()
                        }
                    }
                    second {
                        DiffFileCompose(diff.value)
                    }
                    SourceHorizontalSplitter()
                }
            }
            second {
                MessageCommitAndCloseContainer(close) {
                    rightContainerViewModel.commitFiles(it) {
                        close()
                    }
                }
            }
            SourceVerticalSplitter()
        }
    }
}

@Composable
internal fun StagedFilesCompose(stagedFiles: List<FileCommit>, onClick: (FileCommit) -> Unit, unStage: (FileCommit) -> Unit, revert: (FileCommit) -> Unit) {
    FilesChangedCompose("Staged files",null, stagedFiles, onClick = onClick, onDoubleClick = unStage, listOf("Remove" to unStage, "Revert" to revert))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UnstagedFilesCompose(unStagedFiles: List<FileCommit>, stage: (FileCommit) -> Unit) {
    FilesChangedCompose("Unstaged files", null, files = unStagedFiles, onDoubleClick = stage, itemsContextMenu = listOf("Add" to stage))
}

@Composable
internal fun DiffFileCompose(diff: Diff?) {
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
        EmptyStateOnNullItem(diff) {
            VerticalScrollBox(Modifier.fillMaxSize()) {
                FileDiffCompose(it)
            }
        }
    }
}

@Composable
internal fun MessageCommitAndCloseContainer(onCancel: () -> Unit, onCommit: (String) -> Unit) {
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
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
            adapter = rememberScrollbarAdapter(
                scrollState = scrollState
            )
        )
    }
}