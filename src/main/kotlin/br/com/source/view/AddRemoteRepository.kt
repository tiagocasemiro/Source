package br.com.source.view

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun AddRemoteRepositoryDialog(close: () -> Unit) {
    Dialog(
        onCloseRequest = {
            close()
        },
        title = "Add new remote repository",
    ) {

    }
}