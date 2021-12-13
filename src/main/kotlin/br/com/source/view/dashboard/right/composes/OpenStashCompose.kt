package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.view.common.*
import br.com.source.view.dashboard.right.RightContainerViewModel
import br.com.source.view.model.Diff
import br.com.source.view.model.Stash

@Composable
fun OpenStashCompose(stash: Stash, rightContainerViewModel: RightContainerViewModel) {
    val diffs = rightContainerViewModel.stashDiff.collectAsState()
    val showLoad = rightContainerViewModel.showLoad.collectAsState()
    rightContainerViewModel.stashDiff(stash)
    LoadState(showLoad) {
        StashCompose(diffs.value)
    }
}

@Composable
internal fun StashCompose(diffs: List<Diff>) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        EmptyStateItem(diffs.isNotEmpty(), "The stash modifications will appear here.") {
            Column(
                Modifier.fillMaxSize().verticalScroll(state = scrollState),
            ) {
                diffs.forEach { diff ->
                    key(diff.hashCode()) {
                        FileDiffCompose(diff)
                    }
                    Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
                    Spacer(Modifier.height(20.dp).fillMaxWidth())
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                )
            )
        }
    }
}
