package br.com.source.view.repositories.all

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.emptyString
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.cardFontEmptyWeight
import br.com.source.view.common.StatusStyle.cardFontSize
import br.com.source.view.common.StatusStyle.cardFontStyle
import br.com.source.view.common.StatusStyle.cardFontTitleSize
import br.com.source.view.common.StatusStyle.cardFontTitleWeight
import br.com.source.view.common.StatusStyle.cardFontWeight
import br.com.source.view.common.StatusStyle.cardTextColor
import br.com.source.view.components.SourceButton
import br.com.source.view.repositories.add.AddLocalRepositoryDialog
import br.com.source.view.repositories.add.AddRemoteRepositoryDialog
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.koin.java.KoinJavaComponent.getKoin

@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
@Composable
fun allRepository(openRepository: (LocalRepository) -> Unit) {
    val allRepositoriesViewModel: AllRepositoriesViewModel = getKoin().get()
    val status = remember { mutableStateOf(emptyString()) }
    val repositories = remember { mutableStateOf(allRepositoriesViewModel.all()) }
    val displayAddAlert = remember { mutableStateOf(false) }
    val displayCloneAlert = remember { mutableStateOf(false) }
    val splitterState = rememberSplitPaneState(initialPositionPercentage = 0.4f)

    if(displayAddAlert.value) {
        AddLocalRepositoryDialog() {
            displayAddAlert.value = false
            repositories.value = allRepositoriesViewModel.all()
        }
    }

    if(displayCloneAlert.value) {
        AddRemoteRepositoryDialog {
            displayCloneAlert.value = false
            repositories.value = allRepositoriesViewModel.all()
        }
    }

    HorizontalSplitPane(
        splitPaneState = splitterState
    ) {
        first {
            Box(Modifier
                .fillMaxSize()
                .background(dialogBackgroundColor)
            ) {
                selectRepository(allRepositoriesViewModel, status, repositories, openRepository)
            }
        }
        second {
            Column( modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(cardPadding)
            ) {
                Box(Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Transparent)
                ) {
                    if (status.value.isEmpty())
                        noStatus()
                    else
                        status(status)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement =  Arrangement.End,
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .padding(cardPadding)
                ) {
                    SourceTooltip("Add a local repository." ) {
                        SourceButton("Add") {
                            displayAddAlert.value = true
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    SourceTooltip("Add a remote repository") {
                        SourceButton("Clone") {
                            displayCloneAlert.value = true
                        }
                    }
                }
            }
        }
        SourceHorizontalSplitter()
    }
}

@Composable
fun status(statusRemember: MutableState<String>) {
    val verticalStateList = rememberScrollState()
    val horizontalStateList = rememberScrollState()
    Column(
        Modifier
            .padding(cardPadding)
            .clip(shape = RoundedCornerShape(cardRoundedCorner))
            .fillMaxSize()
    ) {
        Text("\$ git status",
            modifier = Modifier.padding(cardTextPadding),
            color = cardTextColor,
            fontSize = cardFontTitleSize,
            fontStyle = cardFontStyle,
            fontWeight = cardFontTitleWeight,
            fontFamily = Fonts.robotoMono()
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Box(Modifier
                .horizontalScroll(horizontalStateList)
                .verticalScroll(verticalStateList)) {
                Text(
                    statusRemember.value,
                    modifier = Modifier.padding(cardTextPadding),
                    color = cardTextColor,
                    fontSize = cardFontSize,
                    fontStyle = cardFontStyle,
                    fontWeight = cardFontWeight,
                    fontFamily = Fonts.robotoMono()
                )
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = verticalStateList
                )
            )
             HorizontalScrollbar(
                  modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                  adapter = rememberScrollbarAdapter(
                      scrollState = horizontalStateList
                  )
             )
        }
    }
}

@Composable
fun noStatus() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select a repository",
            modifier = Modifier
                .padding(cardTextPadding),
            color = cardTextColor,
            fontSize = 15.sp,
            fontStyle = cardFontStyle,
            fontWeight = cardFontEmptyWeight,
            fontFamily = Fonts.roboto(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun selectRepository(allRepositoriesViewModel: AllRepositoriesViewModel, status: MutableState<String>, repositoryRemember: MutableState<List<LocalRepository>>, openRepository: (LocalRepository) -> Unit) {
    val stateList = rememberLazyListState()
    Column(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(cardRoundedCorner))
            .fillMaxSize()
            .background(cardBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource("images/source-logo.svg"),
                contentDescription = "Source app logo",
                modifier = Modifier.height(45.dp)
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            val selectedIndex = remember { mutableStateOf(-1) }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = stateList
            ) {
                item {
                    Spacer(Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
                }
                itemsIndexed(repositoryRemember.value) { index, repository ->
                    itemRepository(repository, selectedIndex.value == index , onClick = {
                        selectedIndex.value = index
                        status.value = allRepositoriesViewModel.status(repository.workDir)
                    }, onDoubleClick = {
                        openRepository(repository)
                    }, onDeleteClick = {
                        allRepositoriesViewModel.delete(repository)
                        repositoryRemember.value = allRepositoriesViewModel.all()
                        status.value = emptyString()
                    })
                    Spacer(Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun itemRepository(repository: LocalRepository, isSelected: Boolean, onClick: () -> Unit, onDoubleClick: () -> Unit, onDeleteClick: () -> Unit) {
    val onHoverRemoveButton = remember { mutableStateOf(false) }
    val onHoverCard = remember { mutableStateOf(false) }
    val forceCloseCardTooltip = remember { mutableStateOf(false) }
    val forceOpenCardTooltip = remember { mutableStateOf(false) }
    val heightCard = 65.dp
    Card(
        modifier = Modifier.pointerMoveFilter(
            onEnter = {
                onHoverCard.value = true
                false
            },
            onExit = {
                onHoverCard.value = false
                false
            }
        ),
        shape = RoundedCornerShape(0.dp),
        elevation = 0.dp
    ) {
        SourceTooltip("Double click to open ${repository.name} repository", forceCloseCardTooltip, forceOpenCardTooltip) {
            Box {
                Row(
                    modifier = Modifier
                        .height(heightCard)
                        .fillMaxWidth()
                        .background(cardBackgroundColor)
                        .combinedClickable(onDoubleClick = {
                            onDoubleClick()
                        }) {
                            onClick()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource("images/source-repo-icon.svg"),
                        contentDescription = "Source logo repository",
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        painter = painterResource("images/line-vertical.svg"),
                        contentDescription = "Line divide logo and name of repository",
                        modifier = Modifier.size(2.dp, 50.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(
                        Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Text(
                            repository.name,
                            fontSize = 16.sp,
                            fontFamily = Fonts.roboto(),
                            fontWeight = FontWeight.Black,
                            color = itemRepositoryText
                        )
                        Text(
                            repository.workDir,
                            fontSize = 12.sp,
                            fontFamily = Fonts.roboto(),
                            fontWeight = FontWeight.Normal,
                            color = itemRepositoryText
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Card(
                        shape = CircleShape,
                        elevation = 0.dp,
                        modifier = Modifier
                            .pointerMoveFilter(
                                onEnter = {
                                    onHoverRemoveButton.value = true
                                    forceCloseCardTooltip.value = true
                                    false
                                },
                                onExit = {
                                    onHoverRemoveButton.value = false
                                    forceOpenCardTooltip.value = true
                                    false
                                }
                            )
                            .padding(3.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        onDeleteClick()
                                    }
                                )
                            }
                    ) {
                        SourceTooltip("Delete ${repository.name} repository") {
                            Box(
                                modifier = Modifier.background(if (onHoverRemoveButton.value) hoverDeleteRepository else cardBackgroundColor)
                                    .padding(2.dp)
                            ) {
                                Image(
                                    painter = painterResource("images/delete-repository-icon.svg"),
                                    contentDescription = "Delete repo button",
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Spacer(
                        modifier = Modifier.width(5.dp).fillMaxHeight()
                            .background(if (isSelected) itemRepositoryHoveBackground else cardBackgroundColor)
                    )
                }
                Spacer(
                    Modifier.height(heightCard).fillMaxWidth()
                        .background(if (isSelected) itemBranchHoveBackground else Color.Transparent)
                )
            }
        }
    }
}
