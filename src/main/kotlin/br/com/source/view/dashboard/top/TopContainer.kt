package br.com.source.view.dashboard.top

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.emptyString
import br.com.source.model.util.errorOn
import br.com.source.view.common.SourceTooltip
import br.com.source.view.common.showNotification
import br.com.source.view.common.showSuccessNotification
import br.com.source.view.components.TopMenuItem
import br.com.source.view.components.TypeCommunication
import br.com.source.view.components.showDialogContentTwoButton
import br.com.source.view.components.showDialogSingleButton
import br.com.source.view.dashboard.top.composes.CreateStashCompose
import br.com.source.view.dashboard.top.composes.MergeCompose
import br.com.source.view.model.Branch

@Composable
fun TopContainer(localRepository: LocalRepository, close: () -> Unit, leftContainerReload: MutableState<Boolean>) {
    val topContainerViewModel = TopContainerViewModel(localRepository)

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
            val selectedBranch = mutableStateOf(emptyString())
            val branches = topContainerViewModel.localBranches().retryOr(emptyList())
            val message = mutableStateOf(emptyString())
            if(branches.isEmpty()) {
                showDialogSingleButton("Action error", errorOn("Cannot list local branches"), type = TypeCommunication.error)
                return@TopMenuItem
            }
            showDialogContentTwoButton("Merge", content = { MergeCompose(selectedBranch, message, branches) }, labelPositive = "merge", actionPositive = {
                if(selectedBranch.value.isEmpty()) {
                    showNotification("It is necessary to select a branch, to make the merge", TypeCommunication.warn)
                    return@showDialogContentTwoButton
                }
                topContainerViewModel.merge(selectedBranch.value, message.value).onSuccessWithWarnDefaultError(
                    success = {
                        showSuccessNotification("Branch ${selectedBranch.value} merged with success")
                    }
                )
            }, labelNegative = "cancel", size = DpSize(width = 500.dp, height = 500.dp))
        }
        Spacer(Modifier.size(20.dp))
        TopMenuItem("images/menu/stash-menu.svg", "Stash") {
            val message = mutableStateOf(emptyString())
            showDialogContentTwoButton("New stash", content = { CreateStashCompose(message) }, labelPositive = "create", actionPositive = {
                topContainerViewModel.createStash(message.value).onSuccessWithWarnDefaultError(
                    warn = {
                        showNotification(it.message, type = TypeCommunication.warn)
                    },
                    success = {
                        leftContainerReload.value = true
                        showSuccessNotification("Stash created with message ${message.value}")
                    }
                )
            }, labelNegative = "cancel")
        }
        Spacer(Modifier.fillMaxWidth().weight(1f))
        SourceTooltip("Close ${localRepository.name}") {
            TopMenuItem("images/menu/close-menu.svg", "Close") {
               close()
            }
        }
    }
}