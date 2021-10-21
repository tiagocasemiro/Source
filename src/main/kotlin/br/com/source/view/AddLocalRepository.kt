package br.com.source.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowSize
import br.com.source.model.domain.Credential
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.emptyString
import br.com.source.view.common.Fonts
import br.com.source.view.common.StatusStyle
import br.com.source.view.common.StatusStyle.Companion.titleAlertColor
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
    val nameRemember = remember { mutableStateOf(emptyString()) }
    val pathRemember = remember { mutableStateOf(emptyString()) }
    val usernameRemember = remember { mutableStateOf(emptyString()) }
    val passwordRemember = remember { mutableStateOf(emptyString()) }

    Column(
        modifier = Modifier.padding(appPadding)
    ) {
        Text("new repository",
            fontFamily = Fonts.balooBhai2(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
            style = TextStyle(
                color = titleAlertColor
            )
        )
        Spacer(modifier = Modifier.size(appPadding))
        SourceTextField(text = nameRemember, label = "Name")
        Spacer(modifier = Modifier.size(6.dp))
        SourceTextField(text = pathRemember, label = "Path")
        Spacer(modifier = Modifier.size(6.dp))
        SourceTextField(text = usernameRemember, label = "Username")
        Spacer(modifier = Modifier.size(6.dp))
        SourceTextField(text = passwordRemember, label = "Password")
        Spacer(modifier = Modifier.fillMaxSize().weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            SourceButton("cancel", color = StatusStyle.negativeButtonColor) {
                close()
            }
            Spacer(modifier = Modifier.width(10.dp))
            SourceButton("create") {
                val localRepository = LocalRepository(
                    name = nameRemember.value,
                    workDir = pathRemember.value,
                    credential = Credential(
                        username = usernameRemember.value,
                        password = passwordRemember.value
                    )
                )
                addLocalRepositoryViewModel.add(localRepository)
                close()
            }
        }
    }
}