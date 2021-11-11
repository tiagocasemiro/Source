package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HistoryCompose() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Histori")
    }
}