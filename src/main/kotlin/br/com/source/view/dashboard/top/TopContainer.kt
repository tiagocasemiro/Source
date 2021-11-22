package br.com.source.view.dashboard.top

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.*
import br.com.source.view.common.hideLoad
import br.com.source.view.common.showLoad
import br.com.source.view.common.showNotification
import br.com.source.view.common.showSuccessNotification
import br.com.source.view.components.*
import br.com.source.view.dashboard.top.composes.CreateBranchCompose
import br.com.source.view.dashboard.top.composes.CreateStashCompose
import br.com.source.view.dashboard.top.composes.MergeCompose
import br.com.source.view.dashboard.top.composes.PullCompose
import br.com.source.view.model.clearUsedColorOfGraph

@Composable
fun TopContainer(localRepository: LocalRepository, close: () -> Unit, leftContainerReload: () -> Unit, commit: () -> Unit) {
    val topContainerViewModel = TopContainerViewModel(localRepository)

    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        TopMenuItem("images/menu/commit-menu.svg", "Commit modifications on local repo", "Commit", width = 60.dp) {
            commit()
        }
        TopSpaceMenu()
        TopMenuItem("images/menu/push-menu.svg", "Push local changes", "Push", width = 50.dp) {
            showLoad()
            topContainerViewModel.push {
                it.onSuccessWithDefaultError {
                    showSuccessNotification("Push success")
                    hideLoad()
                }
            }
        }
        TopMenuItem("images/menu/pull-menu.svg", "Pull remote changes","Pull", width = 50.dp) {
            topContainerViewModel.remoteBranches { message ->
                val remoteBranches = message.retryOr(emptyList())
                val selectedBranch = mutableStateOf(remoteBranches.first { it.isCurrent }.name)
                showDialogContentTwoButton("New branch", content = { PullCompose(selectedBranch, remoteBranches) }, labelPositive = "pull",
                    actionPositive = {
                        if(selectedBranch.value.isEmpty()) {
                            showDialog("Action error", "Select one branch to make pull", type = TypeCommunication.error)
                            return@showDialogContentTwoButton
                        }
                        hideLoad()
                        topContainerViewModel.pull(selectedBranch.value) {
                            it.onSuccessWithDefaultError {
                                showSuccessNotification("Pull repository with success")
                                hideLoad()
                            }
                        }
                    }, labelNegative = "cancel"
                )
            }
        }
        TopMenuItem("images/menu/fetch-menu.svg", "Fetch changes from remote repository","Fetch", width = 50.dp) {
            showLoad()
            topContainerViewModel.fetch { message ->
                message.onSuccessWithDefaultError {
                    showSuccessNotification(it.takeIf { it.isNotEmpty() }?: "Fetch repository with success")
                    hideLoad()
                }
            }
        }
        TopSpaceMenu()
        TopMenuItem("images/menu/branch-menu.svg", "Create new branch", "Branch", width = 50.dp) {
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
                        showLoad()
                        topContainerViewModel.createNewBranch(name = name.value, switchToNewBranch = switchToNewBranch.value) { message ->
                            message.on(
                                success = {
                                    leftContainerReload()
                                    showSuccessNotification("Branch ${name.value} created with success.")
                                    hideLoad()
                                },
                                error = {
                                    leftContainerReload()
                                    showActionError(it)
                                    hideLoad()
                                }
                            )
                        }
                    }
                }, labelNegative = "cancel", canClose = canClose
            )
        }
        TopMenuItem("images/menu/merge-menu.svg", "Merge branch on ${localRepository.name}", "Merge", width = 50.dp) {
            val selectedBranch = mutableStateOf(emptyString())
            topContainerViewModel.localBranches { message ->
                val branches = message.retryOr(emptyList())
                val messageState = mutableStateOf(emptyString())
                if(branches.isEmpty()) {
                    showDialogSingleButton("Action error", errorOn("Cannot list local branches"), type = TypeCommunication.error)
                    return@localBranches
                }
                showDialogContentTwoButton("Merge", content = { MergeCompose(selectedBranch, messageState, branches) }, labelPositive = "merge", actionPositive = {
                    if(selectedBranch.value.isEmpty()) {
                        showNotification("It is necessary to select a branch, to make the merge", TypeCommunication.warn)
                        return@showDialogContentTwoButton
                    }
                    showLoad()
                    topContainerViewModel.merge(selectedBranch.value, messageState.value) { message ->
                        message.onSuccessWithWarnDefaultError(
                            success = {
                                showSuccessNotification("Branch ${selectedBranch.value} merged with success")
                                hideLoad()
                            }
                        )
                    }
                }, labelNegative = "cancel", size = DpSize(width = 500.dp, height = 500.dp))
            }
        }
        TopSpaceMenu()
        TopMenuItem("images/menu/stash-menu.svg", "Create new stash","Stash", width = 50.dp) {
            val messageStash = mutableStateOf(emptyString())
            showDialogContentTwoButton("New stash", content = { CreateStashCompose(messageStash) }, labelPositive = "create", actionPositive = {
                showLoad()
                topContainerViewModel.createStash(messageStash.value) { message ->
                    message.onSuccessWithWarnDefaultError(
                        warn = {
                            showNotification(it.message, type = TypeCommunication.warn)
                            hideLoad()
                        },
                        success = {
                            leftContainerReload()
                            showSuccessNotification("Stash created with message ${messageStash.value}")
                            hideLoad()
                        }
                    )
                }
            }, labelNegative = "cancel")
        }
        Spacer(Modifier.fillMaxWidth().weight(1f))
        TopMenuItem("images/menu/close-menu.svg", "Close ${localRepository.name}","Close") {
           clearUsedColorOfGraph()
           close()
        }
    }
}

@Composable
fun TopSpaceMenu() {
    Spacer(Modifier.size(30.dp))
}