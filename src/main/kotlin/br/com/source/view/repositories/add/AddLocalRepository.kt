package br.com.source.view.repositories.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.source.model.domain.CredentialType
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
    SourceWindowDialog(close,"Add new local repository", size = DpSize(600.dp, 450.dp)) {
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
    val openDialogFolderChoose = remember { mutableStateOf(false) }
    val pathPrivateKeyRemember = remember { mutableStateOf(emptyString()) }
    val pathPrivateKeyValidationRemember = remember { mutableStateOf(emptyString()) }
    val sshHostRemember = remember { mutableStateOf(emptyString()) }
    val state = remember { mutableStateOf(0) }
    if(openDialogFolderChoose.value) {
        openDialogFolderChoose.value = false
        SourceSwingChooseFolderDialog(pathRemember)
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
            SourceTextField(text = nameRemember, label = "Name", errorMessage = nameValidationRemember, requestFocus = true)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = pathRemember, label = "Path", trailingIcon = {
                SourceChooserFolderButton {
                    openDialogFolderChoose.value = true
                }
            }, errorMessage = pathValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                SegmentedControl("HTTP", "SSH", { state.value = 0 }, { state.value = 1 })
            }
            Spacer(modifier = Modifier.size(12.dp))
            if(state.value == 0) {
                SourceTextField(text = usernameRemember, label = "Username", errorMessage = usernameValidationRemember)
                Spacer(modifier = Modifier.size(6.dp))
                SourceTextField(text = passwordRemember, label = "Password", isPassword = true, errorMessage = passwordValidationRemember)
            } else {
                SourceTextField(text = pathPrivateKeyRemember, label = "Path private key", errorMessage = pathPrivateKeyValidationRemember)
                Spacer(modifier = Modifier.size(6.dp))
                SourceTextField(text = passwordRemember, label = "Password of key", isPassword = true, errorMessage = passwordValidationRemember)
                Spacer(modifier = Modifier.size(6.dp))
                SourceTextField(text = sshHostRemember, label = "Host of SSH (Optional)")
            }
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
                    var isFormValid = nameRemember.validation(listOf(emptyValidation("Name is required")), nameValidationRemember) and
                        pathRemember.validation(listOf(emptyValidation("Path to repository is required")), pathValidationRemember) and
                        passwordRemember.validation(listOf(emptyValidation("Password is required")), passwordValidationRemember)

                    isFormValid = if(state.value == 0) {
                        isFormValid and
                                usernameRemember.validation(listOf(emptyValidation("Username is required")), usernameValidationRemember)
                    } else {
                        isFormValid and
                                pathPrivateKeyRemember.validation(listOf(emptyValidation("Path to ssh key is required")), pathPrivateKeyValidationRemember)
                    }

                    if(isFormValid) {
                        val localRepository = LocalRepository(
                            name = nameRemember.value,
                            workDir = pathRemember.value,
                            credentialType = if(state.value == 0) {
                                CredentialType.HTTP.value
                            } else {
                                CredentialType.SSH.value
                            }
                        ).apply {
                            if(state.value == 0) {
                                username = usernameRemember.value
                                password = passwordRemember.value
                            } else {
                                pathKey = pathPrivateKeyRemember.value
                                passwordKey = passwordRemember.value
                                host = sshHostRemember.value
                            }
                        }
                        addLocalRepositoryViewModel.add(localRepository)
                        close()
                    }
                }
            }
        }
    }
}