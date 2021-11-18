package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.source.view.dashboard.right.RightContainerViewModel

@Composable
fun HistoryCompose(rightContainerViewModel: RightContainerViewModel) {

    rightContainerViewModel.history {

    }


    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Histori")
    }
}
