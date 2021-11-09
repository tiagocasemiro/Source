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
import br.com.source.model.util.*
import br.com.source.view.common.hideLoad
import br.com.source.view.common.showNotification
import br.com.source.view.common.showSuccessNotification
import br.com.source.view.components.*
import br.com.source.view.dashboard.top.composes.CreateBranchCompose
import br.com.source.view.dashboard.top.composes.CreateStashCompose
import br.com.source.view.dashboard.top.composes.MergeCompose

@Composable
fun TopContainer(localRepository: LocalRepository, close: () -> Unit, leftContainerReload: MutableState<Boolean>) {
    val topContainerViewModel = TopContainerViewModel(localRepository)

    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        TopMenuItem("images/menu/commit-menu.svg", "Commit modifications on local repo", "Commit") {
            println("Commit")
        }
        Spacer(Modifier.size(20.dp))
        TopMenuItem("images/menu/push-menu.svg", "Push local changes", "Push") {
            println("Push")
        }
        TopMenuItem("images/menu/pull-menu.svg", "Pull remote changes","Pull") {
            println("Pull")
        }
        TopMenuItem("images/menu/fetch-menu.svg", "Fetch changes from remote repository","Fetch") {
            println("Fetch")
        }
        Spacer(Modifier.size(20.dp))
        TopMenuItem("images/menu/branch-menu.svg", "Create new branch", "Branch") {
            val name = mutableStateOf(emptyString())
            val nameValidation = mutableStateOf(emptyString())
            val switchToNewBranch = mutableStateOf(false)
            val canClose = mutableStateOf(false)
            showDialogContentTwoButton("New branch", content = { CreateBranchCompose(name, nameValidation, switchToNewBranch) }, labelPositive = "create",
                actionPositive = {
                    val isNameValid = name.validation(listOf(emptyValidation("Name is required"), containSpacesValidation("Name cannot contain spaces")), nameValidation)
                    if(isNameValid) {
                        canClose.value = true
                        hideDialog()
                        topContainerViewModel.createNewBranch(name = name.value, switchToNewBranch = switchToNewBranch.value).on(
                            success = {
                                leftContainerReload.value = true
                                showSuccessNotification("Branch ${name.value} created with success.")
                            },
                            error = {
                                leftContainerReload.value = true
                                showActionError(it)
                                hideLoad()
                            }
                        )
                    }
                }, labelNegative = "cancel", canClose = canClose
            )
        }
        TopMenuItem("images/menu/merge-menu.svg", "Merge branch on ${localRepository.name}", "Merge") {
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
        TopMenuItem("images/menu/stash-menu.svg", "Create new stash","Stash") {
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
        TopMenuItem("images/menu/close-menu.svg", "Close ${localRepository.name}","Close") {
           close()
        }
    }
}