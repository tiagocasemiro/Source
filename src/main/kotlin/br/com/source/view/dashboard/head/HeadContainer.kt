package br.com.source.view.dashboard.head

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository

@Composable
fun HeadContainer(localRepository: LocalRepository, close: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(80.dp))
}