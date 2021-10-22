package br.com.source.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.Credential
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

@ExperimentalMaterialApi
@Composable
fun allRepository(allRepositoriesViewModel: AllRepositoriesViewModel, openRepository: (LocalRepository) -> Unit) {
    val status by remember { mutableStateOf(emptyString()) }
    val repositories by remember { mutableStateOf(allRepositoriesViewModel.allRepositories()) }

    val displayAddAlert = remember { mutableStateOf(false) }
    val displayCloneAlert = remember { mutableStateOf(false) }

    if(displayAddAlert.value) {
        AddLocalRepositoryDialog() {
            displayAddAlert.value = false
        }
    }

    if(displayCloneAlert.value) {
        AddRemoteRepositoryDialog {
            displayCloneAlert.value = false
        }
    }

    Row(Modifier.fillMaxSize().background(Color.White)) {
        Box(Modifier
            .fillMaxHeight()
            .width(400.dp)
            .background(backgroundColor)
        ) {
            selectRepository(repositories, openRepository)
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
                if (status.isEmpty())
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
fun status(status: String) {
    Column(
        Modifier
            .padding(cardPadding)
            .clip(shape = RoundedCornerShape(cardRoundedCorner))
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val statusRemember by remember { mutableStateOf(status) }
        Text("\$ git status",
            modifier = Modifier.padding(cardTextPadding),
            color = cardTextColor,
            fontSize = cardFontTitleSize,
            fontStyle = cardFontStyle,
            fontWeight = cardFontTitleWeight,
            fontFamily = Fonts.robotoMono()
        )
        Text(statusRemember,
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
            fontSize = cardFontSize,
            fontStyle = cardFontStyle,
            fontWeight = cardFontEmptyWeight,
            fontFamily = Fonts.roboto(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun selectRepository(repositories: List<LocalRepository>, openRepository: (LocalRepository) -> Unit) {
    val repositoryRemember  by remember { mutableStateOf(repositories) }
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
                modifier = Modifier.height(65.dp)
            )
        }
        LazyColumn {
            item {
                Button(onClick = {
                    openRepository(LocalRepository("","", Credential("", "")))
                }) {
                    Text(text = "Open")
                }
            }
            itemsIndexed(repositoryRemember) { _, repository ->
                itemRepository(repository)
            }
        }
    }
}

@Composable
fun itemRepository(repository: LocalRepository) {
    Text(repository.name)
}