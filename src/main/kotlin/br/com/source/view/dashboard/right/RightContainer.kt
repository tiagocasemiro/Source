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
import br.com.source.view.dashboard.top.TopContainer
import br.com.source.view.model.Stash

@Composable
fun RightContainer(localRepository: LocalRepository, rightState: MutableState<RightState>, close: () -> Unit, leftContainerReload: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(80.dp)) {
            TopContainer(localRepository, close, leftContainerReload) {
                rightState.value = RightState.Commit
            }
        }
        Spacer(modifier = Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
        Box(Modifier.fillMaxSize()) {
            when(val it = rightState.value) {
                is RightState.OpenStash -> OpenStashCompose(it.stash)
                is RightState.History -> HistoryCompose()
                is RightState.Commit -> CommitCompose(localRepository, close, leftContainerReload)
            }
        }
    }
}

sealed class RightState {
    object History: RightState()
    class OpenStash(val stash: Stash): RightState()
    object Commit: RightState()
}