package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.source.model.util.emptyString
import br.com.source.view.common.SourceHorizontalSplitter
import br.com.source.view.common.SourceTooltip
import br.com.source.view.common.SourceVerticalSplitter
import br.com.source.view.common.StatusStyle
import br.com.source.view.common.StatusStyle.negativeButtonColor
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.Diff
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun CommitCompose(close: () -> Unit, rightContainerViewModel: RightContainerViewModel) {
    val hSplitterStateOne = rememberSplitPaneState(0.75f)
    val hSplitterStateTwo = rememberSplitPaneState(0.5f)
    val vSplitterStateOne = rememberSplitPaneState(0.5f)
    val stagedFiles = remember { mutableStateOf(mutableListOf<String>()) }
    val unStagedFiles = remember { mutableStateOf(mutableListOf<String>()) }
    val diff = remember { mutableStateOf<Diff?>(null) }

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
                                    // call view model to retry diff of files
                                },
                                unStage = {
                                   stagedFiles.value.remove(it)
                                   unStagedFiles.value.add(it)
                                }
                            )
                       }
                       second {
                           UnstagedFilesCompose(unStagedFiles) {
                               stagedFiles.value.add(it)
                               unStagedFiles.value.remove(it)
                           }
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
internal fun StagedFilesCompose(stagedFiles: MutableState<MutableList<String>>, onClick: (file: String) -> Unit, unStage: (file: String) -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("staged files")
    }
}

@Composable
internal fun UnstagedFilesCompose(unStagedFiles: MutableState<MutableList<String>>, stage: (file: String) -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("unstaged files")
    }
}

@Composable
internal fun DiffFileCompose(diff: MutableState<Diff?>) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("diff file")
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