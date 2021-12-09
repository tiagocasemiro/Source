package br.com.source.view.dashboard.right

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository
import br.com.source.view.common.itemRepositoryBackground
import br.com.source.view.dashboard.right.composes.CommitCompose
import br.com.source.view.dashboard.right.composes.HistoryCompose
import br.com.source.view.dashboard.right.composes.OpenStashCompose
import br.com.source.view.model.Stash

@Composable
fun RightContainer(localRepository: LocalRepository, rightState: MutableState<RightState>) {
    val rightContainerViewModel = RightContainerViewModel(localRepository)

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
        Box(Modifier.fillMaxSize()) {
            when(val it = rightState.value) {
                is RightState.OpenStash -> OpenStashCompose(it.stash, rightContainerViewModel)
                is RightState.History -> HistoryCompose(rightContainerViewModel)
                is RightState.Commit -> CommitCompose(close = {
                    rightState.value = RightState.History
                }, rightContainerViewModel)
            }
        }
    }
}

sealed class RightState {
    object History: RightState()
    class OpenStash(val stash: Stash): RightState()
    object Commit: RightState()
}