package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.*
import br.com.source.view.dashboard.left.branches.EmptyStateItem
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.CommitItem
import br.com.source.view.model.Diff
import br.com.source.view.model.FileCommit
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@Composable
fun HistoryCompose(rightContainerViewModel: RightContainerViewModel) {
    val hSplitterStateOne = rememberSplitPaneState(0.64f)
    val vSplitterStateOne = rememberSplitPaneState(0.4f)
    val diff = remember { mutableStateOf<Diff?>(null) }
    val filesChanged = remember { mutableStateOf(mutableListOf<FileCommit>() ) }
    val selectedFile = remember { mutableStateOf<FileCommit?>(null) }
    val allCommits: MutableState<List<CommitItem>> = mutableStateOf(emptyList())
    val selectedCommit: MutableState<CommitItem?> = mutableStateOf(null)

    rightContainerViewModel.history {

    }

    VerticalSplitPane(
        splitPaneState = hSplitterStateOne,
        modifier = Modifier.background(StatusStyle.backgroundColor)
    ) {
        first {
            AllCommits(allCommits, selectedCommit)
        }
        second{
            HorizontalSplitPane(
                splitPaneState = vSplitterStateOne,
                modifier = Modifier.background(StatusStyle.backgroundColor)
            ) {
                first {
                    FilesChanged(filesChanged, selectedFile)
                }
                second {
                    DiffCommits(diff)
                }
                SourceHorizontalSplitter()
            }
        }
        SourceVerticalSplitter()
    }
}

@Composable
fun AllCommits(commits: MutableState<List<CommitItem>>, onClick: MutableState<CommitItem?> = mutableStateOf(null)) {
    Spacer(Modifier.fillMaxSize().background(Color.Transparent))
}

@Composable
fun FilesChanged(files: MutableState<MutableList<FileCommit>>, onClick: MutableState<FileCommit?> = mutableStateOf(null),) {
    FilesChangedCompose("Files changed", files = files, onClick = onClick)
}

@Composable
fun DiffCommits(diff: MutableState<Diff?>) {
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
