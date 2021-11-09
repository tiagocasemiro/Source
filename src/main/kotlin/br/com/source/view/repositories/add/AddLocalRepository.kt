package br.com.source.view.repositories.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.domain.Credential
import br.com.source.model.domain.LocalRepository
import br.com.source.model.util.emptyString
import br.com.source.model.util.emptyValidation
import br.com.source.model.util.validation
import br.com.source.view.common.*
import br.com.source.view.common.StatusStyle.backgroundColor
import br.com.source.view.common.StatusStyle.titleAlertColor
import br.com.source.view.components.SourceButton
import br.com.source.view.components.SourceTextField
import br.com.source.view.components.SourceWindowDialog
import org.koin.java.KoinJavaComponent.get

@ExperimentalMaterialApi
@Composable
fun AddLocalRepositoryDialog(close: () -> Unit) {
    SourceWindowDialog(close,"Add new local repository", size = DpSize(600.dp, 400.dp)) {
        AddLocalRepository(close)
    }
}

@Composable
fun AddLocalRepository(close: () -> Unit) {
    val addLocalRepositoryViewModel: AddRepositoryViewModel = get(AddRepositoryViewModel::class.java)
    val nameRemember = remember { mutableStateOf(emptyString()) }
    val pathRemember = remember { mutableStateOf(emptyString()) }
    val usernameRemember = remember { mutableStateOf(emptyString()) }
    val passwordRemember = remember { mutableStateOf(emptyString()) }
    val nameValidationRemember = remember { mutableStateOf(emptyString()) }
    val pathValidationRemember = remember { mutableStateOf(emptyString()) }
    val usernameValidationRemember = remember { mutableStateOf(emptyString()) }
    val passwordValidationRemember = remember { mutableStateOf(emptyString()) }
    val openDialogFileChoose = remember { mutableStateOf(false) }
    if(openDialogFileChoose.value) {
        openDialogFileChoose.value = false
        SourceChooseFolderDialog(pathRemember)
    }

    Box(modifier = Modifier.background(backgroundColor)) {
        Column(
            modifier = Modifier.padding(appPadding).background(backgroundColor)
        ) {
            Text("New repository",
                fontFamily = Fonts.balooBhai2(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                style = TextStyle(
                    color = titleAlertColor
                )
            )
            Spacer(modifier = Modifier.size(appPadding))
            SourceTextField(text = nameRemember, label = "Name", errorMessage = nameValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = pathRemember, label = "Path", trailingIcon = {
                SourceChooserFolderButton {
                    openDialogFileChoose.value = true
                }
            }, errorMessage = pathValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = usernameRemember, label = "Username", errorMessage = usernameValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = passwordRemember, label = "Password", isPassword = true, errorMessage = passwordValidationRemember)
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
                    val isFormValid = nameRemember.validation(listOf(emptyValidation("Name is required")), nameValidationRemember) and
                        pathRemember.validation(listOf(emptyValidation("Path to repository is required")), pathValidationRemember) and
                        usernameRemember.validation(listOf(emptyValidation("Username is required")), usernameValidationRemember) and
                        passwordRemember.validation(listOf(emptyValidation("Password is required")), passwordValidationRemember)

                    if(isFormValid) {
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
    }
}