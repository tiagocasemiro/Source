package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
    val filesChanged = remember { mutableStateOf(listOf<FileCommit>() ) }
    val selectedFile = remember { mutableStateOf<FileCommit?>(null) }
    val allCommits: MutableState<List<CommitItem>> = remember { mutableStateOf(emptyList()) }
    val selectedCommit: MutableState<CommitItem?> = remember { mutableStateOf(null) }

    showLoad()
    rightContainerViewModel.history { message ->
        message.onSuccessWithDefaultError {
            allCommits.value = it
            hideLoad()
        }
    }

    if(selectedCommit.value != null) {
        showLoad()
        rightContainerViewModel.filesFromCommit(selectedCommit.value!!.hash) { message ->
            message.onSuccessWithDefaultError {
                filesChanged.value = it
                hideLoad()
            }
        }
        selectedCommit.value = null
    }

    if(selectedFile.value != null) {
        showLoad()
        rightContainerViewModel.fileDiffOn(selectedFile.value!!.hash!!, selectedFile.value!!.name) { message ->
            message.onSuccessWithDefaultError { diffFile ->
                diff.value = diffFile
                hideLoad()
            }
            selectedFile.value = null
        }
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
    if(commits.value.isNotEmpty()) {
        onClick.value = commits.value.first()
    }
    val stateList = rememberLazyListState()
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(10.dp))
            Text(
                "Tree",
                modifier = Modifier.width(80.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
            Text(
                "Hash",
                modifier = Modifier.width(80.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
            Text(
                "Message",
                modifier = Modifier.fillMaxWidth().weight(1f),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
            Text(
                "Auhor",
                modifier = Modifier.width(180.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
            Text(
                "Date",
                modifier = Modifier.width(180.dp),
                fontFamily = Fonts.balooBhai2(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
        }
        HorizontalDivider()
        Box {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = stateList
            ) {
                val selectedIndex = mutableStateOf(0)
                itemsIndexed(commits.value) { index, commit ->
                    SourceTooltip(commit.resume()) {
                        LineCommitHistory(commit, index, selectedIndex) {
                            onClick.value = commits.value[it]
                        }
                    }
                }
                item {
                    HorizontalDivider()
                    Spacer(Modifier.size(20.dp))
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = stateList
                )
            )
        }
    }
}

@Composable
fun LineCommitHistory(commitItem: CommitItem, index: Int, selectedIndex: MutableState<Int>, onClick: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(if(index == selectedIndex.value) selectedLineItemBackground else if(index % 2 == 0) Color.Transparent else lineItemBackground)
            .clickable {
                selectedIndex.value = index
                onClick(index)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(10.dp))
        Text(
            "",
            modifier = Modifier.width(80.dp),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left
        )
        Text(
            commitItem.abbreviatedHash,
            modifier = Modifier.width(80.dp),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left
        )
        Text(
            commitItem.shortMessage,
            modifier = Modifier.fillMaxWidth().weight(1f),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left,
            maxLines = 1
        )
        Text(
            commitItem.author.split("<").first(),
            modifier = Modifier.width(180.dp),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left
        )
        Text(
            commitItem.date,
            modifier = Modifier.width(180.dp),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun FilesChanged(files: MutableState<List<FileCommit>>, onClick: MutableState<FileCommit?> = mutableStateOf(null),) {
    if(files.value.isNotEmpty()) {
        onClick.value = files.value.first()
    }
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
