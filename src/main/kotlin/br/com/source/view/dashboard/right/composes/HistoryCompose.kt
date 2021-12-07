package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.Message
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
    val showLoad = rightContainerViewModel.showLoad.collectAsState()
    val allCommits: State<Message<List<CommitItem>>> = rightContainerViewModel.commits.collectAsState()
    val filesFromCommit: State<Message<CommitDetail>> = rightContainerViewModel.filesFromCommit.collectAsState()
    val diff:State<Message<Diff?>> = rightContainerViewModel.diff.collectAsState()
    rightContainerViewModel.history()
    val onClickCommitItem: (CommitItem) -> Unit = {
        rightContainerViewModel.selectCommit(it)
    }
    val onClickFileFromCommitItem: (FileCommit) -> Unit = {
        rightContainerViewModel.selectFileFromCommit(it)
    }

    LoadState(showLoad) {
        VerticalSplitPane(
            splitPaneState = hSplitterStateOne,
            modifier = Modifier.background(backgroundColor)
        ) {
            first {
                MessageCompose(allCommits.value) {
                    AllCommits(it, onClickCommitItem)
                }
            }
            second{
                HorizontalSplitPane(
                    splitPaneState = vSplitterStateOne,
                    modifier = Modifier.background(backgroundColor)
                ) {
                    first {
                        MessageCompose((filesFromCommit.value)) {
                            FilesChanged(it.resume, it.filesFromCommit, onClickFileFromCommitItem)
                        }
                        Spacer(Modifier.fillMaxSize())
                    }
                    second {
                        MessageCompose(diff.value) {
                            DiffCommits(it)
                        }
                    }
                    SourceHorizontalSplitter()
                }
            }
            SourceVerticalSplitter()
        }
    }
}

internal val hashColumnWidth = 60.dp
internal val dateColumnWidth = 170.dp

@Composable
private fun AllCommits(commits: List<CommitItem>, onClickCommitItem: (CommitItem) -> Unit) {
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
                itemsIndexed(commits) { index, commit ->
                    Box {
                        HorizontalSplitPane(
                            splitPaneState = vSplitterStateGraphToHash,
                            modifier = Modifier.background(backgroundColor).height(25.dp)
                        ) {
                            first {
                                Row {
                                    Spacer(Modifier.width(10.dp).height(25.dp).background(if(index == selectedIndex.value) selectedLineItemBackground else if(index % 2 == 0) backgroundColor else lineItemBackground))
                                    DrawTreeGraph(commit.drawLine, index, selectedIndex)
                                }
                            }
                            second {
                                LineCommitHistory(commit, index, selectedIndex, vSplitterStateMessageToAuthorizeCallback)
                            }
                            SourceHorizontalSplitter()
                        }
                        Spacer(Modifier.height(25.dp).fillMaxWidth().background(Color.Transparent).clickable {
                            selectedIndex.value = index
                            onClickCommitItem(commits[index])
                        })
                    }
                }
                item {
                    HorizontalDivider()
                    Spacer(Modifier.size(20.dp))
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
                adapter = rememberScrollbarAdapter(
                    scrollState = stateList
                )
            )
        }
    }
}

@Composable
private fun LineCommitHistory(commitItem: CommitItem, index: Int, selectedIndex: MutableState<Int>, splitState: SplitPaneState) {
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
        Spacer(Modifier.width(5.dp))
    }
}

@Composable
private fun FilesChanged(resume:String? = null, files: List<FileCommit>, onClick: (FileCommit) -> Unit) {
    FilesChangedCompose("Files changed", resume, files = files, onClick = onClick)
}

@Composable
private fun DiffCommits(diff: Diff?) {
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
        EmptyStateItem(diff != null) {
            VerticalScrollBox(Modifier.fillMaxSize()) {
                FileDiffCompose(diff!!)
            }
        }
    }
}
