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
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.dashboard.left.branches.EmptyStateItem
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.*
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
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
    val graph: MutableState<List<List<Draw>>> = remember { mutableStateOf(emptyList()) }
    val selectedCommit: MutableState<CommitItem?> = remember { mutableStateOf(null) }

    showLoad()
    rightContainerViewModel.history { message ->
        message.onSuccessWithDefaultError {
            allCommits.value = it
            graph.value = processLog(it)
            hideLoad()
        }
    }

    if(selectedCommit.value != null) {
        showLoad()
        rightContainerViewModel.filesFromCommit(selectedCommit.value!!.hash) { message ->
            message.onSuccessWithDefaultError {
                if(it.isEmpty()) {
                    diff.value = null
                }
                filesChanged.value = it
                hideLoad()
            }
        }
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
        modifier = Modifier.background(backgroundColor)
    ) {
        first {
            AllCommits(graph, allCommits, selectedCommit)
        }
        second{
            HorizontalSplitPane(
                splitPaneState = vSplitterStateOne,
                modifier = Modifier.background(backgroundColor)
            ) {
                first {
                    FilesChanged(selectedCommit.value?.resume(), filesChanged, selectedFile)
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

internal val hashColumnWidth = 60.dp
internal val dateColumnWidth = 170.dp

@Composable
fun AllCommits(graph: MutableState<List<List<Draw>>>, commits: MutableState<List<CommitItem>>, onClick: MutableState<CommitItem?> = mutableStateOf(null)) {
    if(commits.value.isNotEmpty()) {
        onClick.value = commits.value.first()
    }
    val stateList = rememberLazyListState()
    val vSplitterStateGraphToHash = rememberSplitPaneState(0.103f)
    val vSplitterStateMessageToAuthorizeCallback = rememberSplitPaneState(0.7f)

    Column(Modifier.fillMaxSize()) {
        HorizontalSplitPane(
            splitPaneState = vSplitterStateGraphToHash,
            modifier = Modifier.background(backgroundColor).height(25.dp)
        ) {
            first {
                Row(
                    Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Tree",
                        modifier = Modifier,
                        fontFamily = Fonts.balooBhai2(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = itemRepositoryText,
                        textAlign = TextAlign.Left
                    )
                }
            }
            second {
                Row(
                    Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Hash",
                        modifier = Modifier.width(hashColumnWidth),
                        fontFamily = Fonts.balooBhai2(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = itemRepositoryText,
                        textAlign = TextAlign.Left
                    )
                    VerticalDivider()
                    Box(Modifier.weight(1f)) {
                        HorizontalSplitPane(
                            splitPaneState = vSplitterStateMessageToAuthorizeCallback,
                            modifier = Modifier.height(25.dp)
                        ) {
                            first {
                                Row {
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Message",
                                        modifier = Modifier.width(100.dp),
                                        fontFamily = Fonts.balooBhai2(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = itemRepositoryText,
                                        textAlign = TextAlign.Left
                                    )
                                }
                            }
                            second {
                                Row {
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "Auhor",
                                        modifier = Modifier.width(100.dp),
                                        fontFamily = Fonts.balooBhai2(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = itemRepositoryText,
                                        textAlign = TextAlign.Left
                                    )
                                    VerticalDivider()
                                }
                            }
                            SourceHorizontalSplitter()
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Date",
                        modifier = Modifier.width(dateColumnWidth),
                        fontFamily = Fonts.balooBhai2(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = itemRepositoryText,
                        textAlign = TextAlign.Left
                    )
                }
            }
            SourceHorizontalSplitter()
        }
        HorizontalDivider()
        Box {
            clearUsedColorOfGraph()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = stateList
            ) {
                val selectedIndex = mutableStateOf(0)
                itemsIndexed(commits.value) { index, commit ->
                    Box {
                        HorizontalSplitPane(
                            splitPaneState = vSplitterStateGraphToHash,
                            modifier = Modifier.background(backgroundColor).height(25.dp)
                        ) {
                            first {
                                Row {
                                    Spacer(Modifier.width(10.dp).height(25.dp).background(if(index == selectedIndex.value) selectedLineItemBackground else if(index % 2 == 0) backgroundColor else lineItemBackground))
                                    DrawTreeGraph(graph.value[index], index, selectedIndex)
                                }
                            }
                            second {
                                LineCommitHistory(commit, index, selectedIndex, vSplitterStateMessageToAuthorizeCallback)
                            }
                            SourceHorizontalSplitter()
                        }
                        Spacer(Modifier.height(25.dp).fillMaxWidth().background(Color.Transparent).clickable {
                            selectedIndex.value = index
                            onClick.value = commits.value[index]
                        })
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
fun LineCommitHistory(commitItem: CommitItem, index: Int, selectedIndex: MutableState<Int>, splitState: SplitPaneState) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(25.dp)
            .background(if(index == selectedIndex.value) selectedLineItemBackground else if(index % 2 == 0) backgroundColor else lineItemBackground),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(10.dp))
        Text(
            commitItem.abbreviatedHash,
            modifier = Modifier.width(hashColumnWidth),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left
        )
        VerticalDivider()
        Box(Modifier.weight(1f)) {
            HorizontalSplitPane(
                splitPaneState = splitState,
                modifier = Modifier.height(25.dp)
            ) {
                first {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(10.dp))
                        Text(
                            commitItem.shortMessage,
                            modifier = Modifier,
                            fontFamily = Fonts.roboto(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = itemRepositoryText,
                            textAlign = TextAlign.Left,
                            maxLines = 1
                        )
                    }
                }
                second {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(10.dp))
                        Text(
                            commitItem.author.split("<").first(),
                            modifier = Modifier,
                            fontFamily = Fonts.roboto(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = itemRepositoryText,
                            textAlign = TextAlign.Left
                        )
                    }
                }
                SourceHorizontalSplitter()
            }
        }
        VerticalDivider()
        Spacer(Modifier.width(10.dp))
        Text(
            commitItem.date,
            modifier = Modifier.width(dateColumnWidth),
            fontFamily = Fonts.roboto(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = itemRepositoryText,
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun FilesChanged(resume:String? = null, files: MutableState<List<FileCommit>>, onClick: MutableState<FileCommit?> = mutableStateOf(null),) {
    if(files.value.isNotEmpty()) {
        onClick.value = files.value.first()
    }
    FilesChangedCompose("Files changed", resume, files = files, onClick = onClick)
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
