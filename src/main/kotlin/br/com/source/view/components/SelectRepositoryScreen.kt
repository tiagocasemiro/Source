package br.com.source.view.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import br.com.source.viewmodel.SelectRepositoryViewModel


@Composable
fun SelectRepository(loadRepositoryViewModel: SelectRepositoryViewModel) {
    val status by remember { mutableStateOf(loadRepositoryViewModel.status()) }
    Status(status)
}

@Composable
fun Status(status: String) {
    Column(

    ) {
        val statusRemember by remember { mutableStateOf(status) }

        Text(statusRemember)
    }
}

@Composable
fun allRepositories() {

}

