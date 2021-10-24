package br.com.source.view

import androidx.compose.foundation.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.emptyString
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.Companion.backgroundColor
import br.com.source.view.common.StatusStyle.Companion.cardFontEmptyWeight
import br.com.source.view.common.StatusStyle.Companion.cardFontSize
import br.com.source.view.common.StatusStyle.Companion.cardFontStyle
import br.com.source.view.common.StatusStyle.Companion.cardFontTitleSize
import br.com.source.view.common.StatusStyle.Companion.cardFontTitleWeight
import br.com.source.view.common.StatusStyle.Companion.cardFontWeight
import br.com.source.view.common.StatusStyle.Companion.cardTextColor
import br.com.source.view.components.SourceButton
import br.com.source.viewmodel.AllRepositoriesViewModel
import org.koin.java.KoinJavaComponent.getKoin

@ExperimentalMaterialApi
@Composable
fun allRepository(openRepository: (LocalRepository) -> Unit) {
    val allRepositoriesViewModel: AllRepositoriesViewModel = getKoin().get()
    val status = remember { mutableStateOf(emptyString()) }
    val repositories = remember { mutableStateOf(allRepositoriesViewModel.all()) }
    val displayAddAlert = remember { mutableStateOf(false) }
    val displayCloneAlert = remember { mutableStateOf(false) }

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

    Row(Modifier.fillMaxSize().background(backgroundColor)) {
        Box(Modifier
            .fillMaxHeight()
            .width(400.dp)
            .background(cardBackgroundColor)
        ) {
            selectRepository(allRepositoriesViewModel, status, repositories, openRepository)
        }
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
                modifier = Modifier.background(Color.Transparent).fillMaxWidth().padding(cardPadding)

            ) {
                SourceButton("Add") {
                    displayAddAlert.value = true
                }
                Spacer(modifier = Modifier.width(10.dp))
                SourceButton("Clone") {
                    displayCloneAlert.value = true
                }
            }
        }
    }
}

@Composable
fun status(statusRemember: MutableState<String>) {
    Column(
        Modifier
            .padding(cardPadding)
            .clip(shape = RoundedCornerShape(cardRoundedCorner))
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        Text("\$ git status",
            modifier = Modifier.padding(cardTextPadding),
            color = cardTextColor,
            fontSize = cardFontTitleSize,
            fontStyle = cardFontStyle,
            fontWeight = cardFontTitleWeight,
            fontFamily = Fonts.robotoMono()
        )
        Text(statusRemember.value,
            modifier = Modifier.padding(cardTextPadding),
            color = cardTextColor,
            fontSize = cardFontSize,
            fontStyle = cardFontStyle,
            fontWeight = cardFontWeight,
            fontFamily = Fonts.robotoMono()
        )
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
            .padding(cardPadding)
            .clip(shape = RoundedCornerShape(cardRoundedCorner))
            .fillMaxSize()
            .background(cardBackgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource("images/source-logo.svg"),
                contentDescription = "Source app logo",
                modifier = Modifier.height(50.dp)
            )
        }
        Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(end = 12.dp),
                state = stateList
            ) {
                itemsIndexed(repositoryRemember.value) { _, repository ->
                    itemRepository(repository, onClick = {
                        status.value = allRepositoriesViewModel.status(repository.workDir)
                    }, onDoubleClick = {
                        openRepository(repository)
                    }, onDeleteClick = {
                        allRepositoriesViewModel.delete(repository)
                        repositoryRemember.value = allRepositoriesViewModel.all()
                        status.value = emptyString()
                    })
                    Spacer(modifier = Modifier.height(5.dp))
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun itemRepository(repository: LocalRepository, onClick: () -> Unit, onDoubleClick: () -> Unit, onDeleteClick: () -> Unit) {
    val onHoverRemoveButton = remember { mutableStateOf(false) }
    val onHoverCard = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.padding(cardPadding).pointerMoveFilter(
            onEnter = {
                onHoverCard.value = true
                false
            },
            onExit = {
                onHoverCard.value = false
                false
            }
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .background(if(onHoverCard.value) itemRepositoryHoveBackground else itemRepositoryBackground)
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
                Text(repository.name,
                    fontSize = 16.sp,
                    fontFamily = Fonts.roboto(),
                    fontWeight = FontWeight.Black,
                    color = itemRepositoryText
                )
                Text(repository.workDir,
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
                            false
                        },
                        onExit = {
                            onHoverRemoveButton.value = false
                            false
                        }
                    )
                    .padding(3.dp)
                    .clickable {
                        onDeleteClick()
                    }
            ) {
                Box(
                    modifier = Modifier.background(if(onHoverRemoveButton.value) hoverDeleteRepository else if(onHoverCard.value) itemRepositoryHoveBackground else itemRepositoryBackground).padding(2.dp)
                ) {
                    Image(
                        painter = painterResource("images/delete-repository-icon.svg"),
                        contentDescription = "Delete repo button",
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
    }
}
