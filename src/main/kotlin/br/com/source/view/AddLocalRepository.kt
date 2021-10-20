package br.com.source.view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.com.source.view.common.Fonts
import br.com.source.view.components.SourceTextField
import br.com.source.viewmodel.AddLocalRepositoryViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
fun AddLocalRepositoryDialog(close: () -> Unit) {
    Dialog(
        onCloseRequest = {
            close()
        },
        title = "Add new local repository",
    ) {
        AddLocalRepository(close)
    }
}


@Composable
fun AddLocalRepository(close: () -> Unit) {
    val addLocalRepositoryViewModel: AddLocalRepositoryViewModel = get(AddLocalRepositoryViewModel::class.java)

    Column {
        Text("Add repository",
            fontFamily = Fonts.balooBhai2(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 40.sp
        )
        SourceTextField("Url")
        SourceTextField("local")
        SourceTextField(
            label = "label",
            text = "message",
            placeholder = "place"
        )
    }
}