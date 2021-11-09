package br.com.source.view.dashboard.top.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
      SourceTextField(text = message, label = "Message")
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
         fontSize = 14.sp,
         fontWeight = FontWeight.Medium,
         color = itemRepositoryText
      )
      Spacer(Modifier.size(5.dp))
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
            SourceTextField(text = message, label = "Message")
         }
      }
   }
}


@Composable
fun CreateBranchCompose(name: MutableState<String>, nameValidation: MutableState<String>, switchToNewBranch: MutableState<Boolean>) {
   Column(Modifier.fillMaxSize().background(dialogBackgroundColor)) {
      SourceTextField(text = name, label = "Message", errorMessage = nameValidation)
      SourceCheckBox("Switch to new branch", switchToNewBranch)
   }
}