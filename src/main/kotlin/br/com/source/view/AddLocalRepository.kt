package br.com.source.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.com.source.view.components.CustomTextField
import br.com.source.view.components.SourceTextField
import br.com.source.viewmodel.AddLocalRepositoryViewModel
import org.koin.java.KoinJavaComponent.get

@Composable
fun addLocalRepositoryDialog(close: () -> Unit) {
    Dialog(
        onCloseRequest = {
            close()
        },
        title = "Add new local repository",
    ) {
        addLocalRepository(close)
    }
}


@Composable
fun addLocalRepository(close: () -> Unit) {
    val addLocalRepositoryViewModel: AddLocalRepositoryViewModel = get(AddLocalRepositoryViewModel::class.java)

    Column {
        Text("Add repository")
        SourceTextField("Url")
        SourceTextField("local")
        CustomTextField(
            leadingIcon = {
                Icon(
                    Icons.Filled.Search,
                    null,
                    tint = LocalContentColor.current.copy(alpha = 0.3f)
                )
            },
            trailingIcon = null,
            modifier = Modifier
                .background(
                    MaterialTheme.colors.background,
                    RoundedCornerShape(percent = 50)
                )
                .padding(4.dp)
                .height(30.dp),
            fontSize = 14.sp,
            placeholderText = "Search"
        )
    }
}