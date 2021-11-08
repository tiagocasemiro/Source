package br.com.source.view.dashboard.top

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.sleep
import br.com.source.view.common.SourceTooltip
import br.com.source.view.components.TopMenuItem

@Composable
fun TopContainer(localRepository: LocalRepository, close: () -> Unit) {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        TopMenuItem("images/menu/commit-menu.svg", "Commit") {
            println("Commit")
        }
        Spacer(Modifier.size(20.dp))
        TopMenuItem("images/menu/push-menu.svg", "Push") {
            println("Push")
        }
        TopMenuItem("images/menu/pull-menu.svg", "Pull") {
            println("Pull")
        }
        TopMenuItem("images/menu/fetch-menu.svg", "Fetch") {
            println("Fetch")
        }
        Spacer(Modifier.size(20.dp))
        TopMenuItem("images/menu/branch-menu.svg", "Branch") {
            println("Branch")
        }
        TopMenuItem("images/menu/merge-menu.svg", "Merge") {
            println("Merge")
        }
        Spacer(Modifier.size(20.dp))
        TopMenuItem("images/menu/stash-menu.svg", "Stash") {
            println("Stash")
        }
        Spacer(Modifier.fillMaxWidth().weight(1f))
        SourceTooltip("Close ${localRepository.name}") {
            TopMenuItem("images/menu/close-menu.svg", "Close") {
                sleep(300) {
                    close()
                }
            }
        }
    }
}