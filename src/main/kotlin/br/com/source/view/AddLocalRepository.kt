package br.com.source.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowSize
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle
import br.com.source.view.common.appPadding
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.components.SourceWindowDialog
import br.com.source.viewmodel.AddLocalRepositoryViewModel
import org.koin.java.KoinJavaComponent.get


@ExperimentalMaterialApi
@Composable
fun AddLocalRepositoryDialog(close: () -> Unit) {
    SourceWindowDialog(close,"Add new local repository", size = WindowSize(600.dp, 400.dp)) {
        AddLocalRepository(close)
    }
}


@Composable
fun AddLocalRepository(close: () -> Unit) {
    val addLocalRepositoryViewModel: AddLocalRepositoryViewModel = get(AddLocalRepositoryViewModel::class.java)

    Column(
        modifier = Modifier.padding(appPadding)
    ) {
        Text("new repository",
            fontFamily = Fonts.balooBhai2(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
        )
        Spacer(modifier = Modifier.size(appPadding))
        SourceTextField(text = "", label = "Name")
        Spacer(modifier = Modifier.size(6.dp))
        SourceTextField(text = "", label = "Path")
        Spacer(modifier = Modifier.size(6.dp))
        SourceTextField(text = "", label = "Username")
        Spacer(modifier = Modifier.size(6.dp))
        SourceTextField(text = "", label = "Password")
        Spacer(modifier = Modifier.fillMaxSize().weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            SourceButton("cancel", color = StatusStyle.negativeButtonColor) {
                // implement
                close()
            }
            Spacer(modifier = Modifier.width(10.dp))
            SourceButton("create") {
                // implement
                close()
            }
        }
    }
}