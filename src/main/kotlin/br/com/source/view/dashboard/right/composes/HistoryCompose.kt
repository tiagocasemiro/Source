package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.util.detectTapGesturesWithContextMenu
import br.com.source.model.util.emptyString
import br.com.source.model.util.emptyValidation
import br.com.source.model.util.validation
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.components.*
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.*
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.SplitPaneState
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryCompose(rightContainerViewModel: RightContainerViewModel, branch: Branch? = null, onCreateTag: () -> Unit) {
    val hSplitterStateOne = rememberSplitPaneState(0.64f)
    val vSplitterStateOne = rememberSplitPaneState(0.5f)
    val showLoad = rightContainerViewModel.showLoad.collectAsState()
    val allCommits: State<List<CommitItem>> = rightContainerViewModel.commits.collectAsState()
    val commitDetailState: State<CommitDetail?> = rightContainerViewModel.filesFromCommit.collectAsState()
    val diff:State<Diff?> = rightContainerViewModel.diff.collectAsState()
    val selectedIndex: State<Int> = rightContainerViewModel.selectedIndex.collectAsState()
    rightContainerViewModel.history(branch)

    LoadState(showLoad) {
        VerticalSplitPane(
            splitPaneState = hSplitterStateOne,
            modifier = Modifier.background(backgroundColor)
        ) {
            first {
                AllCommits(selectedIndex, allCommits.value,
                    onClickCommitItem = { indexCommit ->
                        rightContainerViewModel.selectCommit(indexCommit)
                    },
                    onTag = { commitItem ->
                       showAlertCreateTag { name: String ->
                           rightContainerViewModel.createTag(name, commitItem.hash) {
                               onCreateTag()
                           }
                       }
                    },
                )
            }
            second{
                HorizontalSplitPane(
                    splitPaneState = vSplitterStateOne,
                    modifier = Modifier.background(backgroundColor)
                ) {
                    first {
                        CommitDetails(commitDetailState.value) { fileCommit ->
                            rightContainerViewModel.selectFileFromCommit(fileCommit)
                        }
                    }
                    second {
                        DiffCommits(diff.value)
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

@ExperimentalFoundationApi
@Composable
private fun AllCommits(selectedIndex: State<Int>, commits: List<CommitItem>, onClickCommitItem: (Int) -> Unit, onTag: (CommitItem) -> Unit) {
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
                        modifier = Modifier.weight(1f),
                        fontFamily = Fonts.balooBhai2(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = itemRepositoryText,
                        textAlign = TextAlign.Left
                    )
                    indicationResize()
                }
            }
            second {
                Row(
                    Modifier.background(cardBackgroundColor).fillMaxWidth().height(25.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    indicationResize()
                    Row(Modifier.width(hashColumnWidth)) {
                        Text(
                            "Hash",
                            fontFamily = Fonts.balooBhai2(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = itemRepositoryText,
                            textAlign = TextAlign.Left
                        )
                    }
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
                                        modifier = Modifier.weight(1f),
                                        fontFamily = Fonts.balooBhai2(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = itemRepositoryText,
                                        textAlign = TextAlign.Left
                                    )
                                    indicationResize()
                                }
                            }
                            second {
                                Row {
                                    indicationResize()
                                    Text(
                                        "Auhor",
                                        modifier = Modifier.width(100.dp),
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
                    }
                    VerticalDivider()
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
                    Spacer(Modifier.width(5.dp))
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
                itemsIndexed(commits) { index, commit ->
                    key(commit.hash) {
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
                            val items = {
                                listOf(
                                    ContextMenuItem("Create Tag") {
                                        onTag(commit)
                                    },
                                )
                            }
                            val state = ContextMenuState()
                            ContextMenuArea(items = items, state = state) {
                                Spacer(Modifier
                                    .height(25.dp)
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .detectTapGesturesWithContextMenu(state = state,
                                        onTap = {
                                            onClickCommitItem(index)
                                        }
                                    )
                                )
                            }
                        }
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
private fun LineCommitHistory(commitItem: CommitItem, index: Int, selectedIndex: State<Int>, splitState: SplitPaneState) {
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
                        var haveSpace = false
                        if(commitItem.node.branches.isNotEmpty()) {
                            Spacer(Modifier.width(10.dp))
                            BranchOnHistory(commitItem.getCommitColor())
                            haveSpace = true
                        }
                        if(commitItem.node.tags.isNotEmpty()) {
                            Spacer(Modifier.width(if(haveSpace) 5.dp else 10.dp))
                            TagOnHistory(commitItem.getCommitColor())
                            haveSpace = true
                        }
                        Spacer(Modifier.width(if(haveSpace) 5.dp else 10.dp))
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
private fun CommitDetails(commitDetailOptional: CommitDetail? = null, onClick: (FileCommit) -> Unit) {
    val state = remember { mutableStateOf(0) }
    EmptyStateOnNullItem(commitDetailOptional) { commitDetail ->
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                Modifier.background(cardBackgroundColor).fillMaxWidth().height(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                SegmentedControl(state,"Files", "Commit", { state.value = 0 }, { state.value = 1 })
            }
            HorizontalDivider()
            if(state.value == 0) {
                FilesChangedCompose(null, files = commitDetail.filesFromCommit, onClick = onClick)
            } else {
                CommitDescription(commitDetail)
            }
        }
    }
}

@Composable
private fun CommitDescription(commitDetail: CommitDetail) {
    val width = remember { mutableStateOf(0.dp) }
    Box {
        FullScrollBox(Modifier.fillMaxSize(), maxWidth = width) {
            Column {
                RowCommitDetail("Hash:", commitDetail.hash, lineItemBackground, width.value)
                RowCommitDetail("Author:", commitDetail.author, backgroundColor, width.value)
                RowCommitDetail("Date:", commitDetail.date, lineItemBackground, width.value)
                RowCommitDetail("Message:", commitDetail.message(), backgroundColor, width.value)
                if(commitDetail.branches.isNotEmpty()) {
                    RowCommitDetail("Branches:", commitDetail.branches.joinToString("\n") { it }, lineItemBackground, width.value)
                }
                if(commitDetail.tags.isNotEmpty()) {
                    val background = if(commitDetail.branches.isNotEmpty()) backgroundColor else lineItemBackground
                    RowCommitDetail("Tags:", commitDetail.tags.joinToString("\n") { it }, background, width.value)
                }
                Spacer(Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun RowCommitDetail(label: String, value: String, background: Color, width: Dp) {
    Box(Modifier.background(background)) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                label,
                modifier = Modifier.padding(start = 10.dp).width(60.dp),
                fontFamily = Fonts.roboto(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = itemRepositoryText,
                textAlign = TextAlign.Left
            )
            Text(
                value,
                modifier = Modifier.padding(start = 10.dp),
                fontFamily = Fonts.roboto(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = itemRepositoryText,
                textAlign = TextAlign.Left,
            )
        }
        Spacer(Modifier.fillMaxHeight().background(background).width(width))
    }
}

@Composable
private fun DiffCommits(diff: Diff?) {
    EmptyStateItem(diff != null) {
        VerticalScrollBox(Modifier.fillMaxSize()) {
            FileDiffCompose(diff!!)
        }
    }
}

@Composable
fun BranchOnHistory(color: Color) {
    Box(
        Modifier.background(color, RoundedCornerShape(6.dp)).border(1.dp, Color.White , RoundedCornerShape(3.dp)).padding(3.dp)
    ) {
        Image(
            painter = painterResource("images/branch_white.svg"),
            contentDescription = "Branches on commit",
            modifier = Modifier
                .background(Color.Transparent)
                .size(11.dp, 11.dp)
        )
    }
}

@Composable
fun TagOnHistory(color: Color) {
    Box(
        Modifier.background(color, RoundedCornerShape(6.dp)).border(1.dp, Color.White , RoundedCornerShape(3.dp)).padding(3.dp)
    ) {
        Image(
            painter = painterResource("images/tag_white.svg"),
            contentDescription = "Tags on commit",
            modifier = Modifier
                .background(Color.Transparent)
                .size(11.dp, 11.dp)
        )
    }
}

fun showAlertCreateTag(onResult: (name: String) -> Unit) {
    val name = mutableStateOf(emptyString())
    val nameValidation = mutableStateOf(emptyString())
    val canClose = mutableStateOf(false)
    showDialogContentTwoButton("Create tag",
        content = { CreateTag(name, nameValidation) },
        labelPositive = "create",
        actionPositive = {
            if(name.validation(listOf(emptyValidation("Name is required")), nameValidation)) {
                onResult(name.value)
                hideDialog()
            }
        },
        labelNegative = "cancel",
        canClose = canClose
    )
}

@Composable
fun CreateTag(name: MutableState<String>, nameValidation: MutableState<String>) {
    Column(Modifier.fillMaxSize().background(dialogBackgroundColor)) {
        SourceTextField(text = name, label = "Name", requestFocus = true, errorMessage = nameValidation)
    }
}
