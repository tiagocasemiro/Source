package br.com.source.view.dashboard.center

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.cardBackgroundColor
import br.com.source.view.dashboard.CenterState

@Composable
fun CenterContainer(localRepository: LocalRepository, centerState: MutableState<CenterState>) {
    when (centerState.value) {
        is CenterState.Log -> CenterLogContainer(localRepository)
        is CenterState.OpenStash -> CenterStashContainer(localRepository)
    }
}

@Composable
fun CenterLogContainer(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize())
}

@Composable
fun CenterStashContainer(localRepository: LocalRepository) {
    Box(Modifier.fillMaxSize().background(cardBackgroundColor))
}
