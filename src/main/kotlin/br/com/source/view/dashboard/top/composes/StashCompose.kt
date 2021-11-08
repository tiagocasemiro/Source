package br.com.source.view.dashboard.top.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import br.com.source.view.common.StatusStyle
import br.com.source.view.components.SourceTextField

@Composable
fun CreateStashCompose(message: MutableState<String>) {
   Column(Modifier.fillMaxSize().background(StatusStyle.backgroundColor)) {
      SourceTextField(text = message, label = "Message")
   }
}