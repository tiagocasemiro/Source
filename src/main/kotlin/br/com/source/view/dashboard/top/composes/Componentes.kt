package br.com.source.view.dashboard.top.composes

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.view.common.Fonts
import br.com.source.view.common.dialogBackgroundColor
import br.com.source.view.common.itemRepositoryText
import br.com.source.view.components.SourceCheckBox
import br.com.source.view.components.SourceRadioButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.model.Branch

@Composable
fun CreateStashCompose(message: MutableState<String>) {
   Column(Modifier.fillMaxSize().background(dialogBackgroundColor)) {
      SourceTextField(text = message, label = "Message", requestFocus = true)
   }
}

@Composable
fun MergeCompose(selectedBranch: MutableState<String>, message: MutableState<String>, branches: List<Branch>) {
   val currentBranch = branches.first { it.isCurrent }.name
   val stateList = rememberLazyListState()
   Column(Modifier.background(dialogBackgroundColor)) {
      Text(
         text = buildAnnotatedString {
            append("Select a branch to merge")
            if(currentBranch.isNotBlank()) {
               append(" on ")
               withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                  append(currentBranch)
               }
            }
         },
         modifier = Modifier.fillMaxWidth(),
         fontFamily = Fonts.roboto(),
         fontSize = 16.sp,
         fontWeight = FontWeight.Medium,
         color = itemRepositoryText
      )
      Spacer(Modifier.size(5.dp))
      Box {
         LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = stateList
         ) {
            itemsIndexed(branches) { _, branch ->
               if(branch.isCurrent.not())
                  SourceRadioButton(branch.clearName, selectedBranch)
            }
            item {
               Spacer(Modifier.size(20.dp))
               SourceTextField(text = message, label = "Message", lines = 3)
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
fun CreateBranchCompose(name: MutableState<String>, nameValidation: MutableState<String>, switchToNewBranch: MutableState<Boolean>) {
   Column(Modifier.fillMaxSize().background(dialogBackgroundColor)) {
      SourceTextField(text = name, label = "Message", errorMessage = nameValidation, requestFocus = true)
      SourceCheckBox("Switch to new branch", switchToNewBranch)
   }
}

@Composable
fun PullCompose(selectedBranch: MutableState<String>, branches: List<Branch>) {
   val stateList = rememberLazyListState()
   Column(Modifier.background(dialogBackgroundColor)) {
      Text(
         text = "Remote branches",
         modifier = Modifier.fillMaxWidth(),
         fontFamily = Fonts.roboto(),
         fontSize = 16.sp,
         fontWeight = FontWeight.Medium,
         color = itemRepositoryText
      )
      Spacer(Modifier.size(5.dp))
      Box {
         LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = stateList
         ) {
            itemsIndexed(branches) { _, branch ->
               SourceRadioButton(branch.clearName, selectedBranch, branch.isCurrent)
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