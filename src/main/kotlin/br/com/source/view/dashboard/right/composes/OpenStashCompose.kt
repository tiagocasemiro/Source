package br.com.source.view.dashboard.right.composes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.view.common.FileDiffCompose
import br.com.source.view.common.itemRepositoryBackground
import br.com.source.view.common.paddingScrollBar
import br.com.source.view.dashboard.left.branches.EmptyStateItem
import br.com.source.view.model.Diff

@Composable
fun OpenStashCompose(diffs: List<Diff>) {
    val stateList = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        EmptyStateItem(diffs.isEmpty(), "The stash modifications will appear here.") {
            Column(
                Modifier.fillMaxSize().verticalScroll(state = stateList),
            ) {
                diffs.forEach { diff ->
                    FileDiffCompose(diff)
                    Spacer(Modifier.height(1.dp).fillMaxWidth().background(itemRepositoryBackground))
                    Spacer(Modifier.height(20.dp).fillMaxWidth())
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(horizontal = paddingScrollBar),
                adapter = rememberScrollbarAdapter(
                    scrollState = stateList
                )
            )
        }
    }
}

