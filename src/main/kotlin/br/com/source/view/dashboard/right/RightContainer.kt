package br.com.source.view.dashboard.right

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.view.common.itemRepositoryBackground
import br.com.source.view.dashboard.right.composes.CommitCompose
import br.com.source.view.dashboard.right.composes.HistoryCompose
import br.com.source.view.dashboard.right.composes.OpenStashCompose
import br.com.source.view.model.Branch
import br.com.source.view.model.Stash

@Composable
fun RightContainer(rightContainerViewModel: RightContainerViewModel, rightState: MutableState<RightState>, onCreateTag: () -> Unit) {

    Column(Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.background(itemRepositoryBackground).height(1.dp).fillMaxWidth())
        Box(Modifier.fillMaxSize()) {
            when(val it = rightState.value) {
                is RightState.OpenStash -> OpenStashCompose(it.stash, rightContainerViewModel)
                is RightState.History -> HistoryCompose(rightContainerViewModel, it.branch, onCreateTag)
                is RightState.Commit -> CommitCompose(close = {
                    rightState.value = RightState.History()
                }, rightContainerViewModel)
            }
        }
    }
}

sealed class RightState {
    class History(val branch: Branch? = null): RightState()
    class OpenStash(val stash: Stash): RightState()
    object Commit: RightState()
}