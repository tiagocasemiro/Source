package br.com.source.view.repositories.edit

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
import br.com.source.model.domain.CredentialType.HTTP
import br.com.source.model.domain.CredentialType.SSH
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
fun EditLocalRepositoryDialog(localRepository: LocalRepository, close: () -> Unit) {
    SourceWindowDialog(close,"Edit local repository", size = DpSize(600.dp, 450.dp)) {
        EditLocalRepository(localRepository, close)
    }
}

@Composable
fun EditLocalRepository(localRepository: LocalRepository, close: () -> Unit) {
    val editLocalRepositoryViewModel: EditRepositoryViewModel = get(EditRepositoryViewModel::class.java)
    val nameRemember = remember { mutableStateOf(localRepository.name) }
    val pathRemember = remember { mutableStateOf(localRepository.workDir) }
    val usernameRemember = remember { mutableStateOf(localRepository.username) }
    val passwordRemember = remember { mutableStateOf(localRepository.password) }
    val passwordKeyRemember = remember { mutableStateOf(localRepository.passwordKey) }
    val pathPrivateKeyRemember = remember { mutableStateOf(localRepository.pathKey) }
    val sshHostRemember = remember { mutableStateOf(localRepository.host) }
    val state = remember { mutableStateOf(if(localRepository.credentialType == SSH.value) { 1 } else { 0 }) }
    val nameValidationRemember = remember { mutableStateOf(emptyString()) }
    val usernameValidationRemember = remember { mutableStateOf(emptyString()) }
    val passwordValidationRemember = remember { mutableStateOf(emptyString()) }
    val passwordKeyValidationRemember = remember { mutableStateOf(emptyString()) }
    val pathPrivateKeyValidationRemember = remember { mutableStateOf(emptyString()) }

    Box(modifier = Modifier.background(backgroundColor)) {
        Column(
            modifier = Modifier.padding(appPadding).background(backgroundColor)
        ) {
            Text("Edit ${localRepository.name} repository",
                fontFamily = Fonts.balooBhai2(),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                style = TextStyle(
                    color = titleAlertColor
                )
            )
            Spacer(modifier = Modifier.size(appPadding))
            SourceTextField(text = nameRemember, label = "Name", requestFocus = true, errorMessage = nameValidationRemember)
            Spacer(modifier = Modifier.size(6.dp))
            SourceTextField(text = pathRemember, label = "Path", readOnly = true)
            Spacer(modifier = Modifier.size(6.dp))
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                SegmentedControl(state, "HTTP", "SSH", { state.value = 0 }, { state.value = 1 })
            }
            Spacer(modifier = Modifier.size(12.dp))
            if(state.value == 0) {
                SourceTextField(text = usernameRemember, label = "Username", errorMessage = usernameValidationRemember)
                Spacer(modifier = Modifier.size(6.dp))
                SourceTextField(text = passwordRemember, label = "Password", isPassword = true, errorMessage = passwordValidationRemember)
            } else {
                SourceTextField(text = pathPrivateKeyRemember, label = "Path private key", errorMessage = pathPrivateKeyValidationRemember)
                Spacer(modifier = Modifier.size(6.dp))
                SourceTextField(text = passwordKeyRemember, label = "Password of key", isPassword = true, errorMessage = passwordKeyValidationRemember)
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
                SourceButton("save") {
                    val isFormValid = if(state.value == 0) {
                        nameRemember.validation(listOf(emptyValidation("Name is required")), nameValidationRemember) and
                        usernameRemember.validation(listOf(emptyValidation("Username is required")), usernameValidationRemember) and
                        passwordRemember.validation(listOf(emptyValidation("Password is required")), passwordValidationRemember)
                    } else {
                        nameRemember.validation(listOf(emptyValidation("Name is required")), nameValidationRemember) and
                        passwordKeyRemember.validation(listOf(emptyValidation("Password of key is required")), passwordKeyValidationRemember) and
                        pathPrivateKeyRemember.validation(listOf(emptyValidation("Path to ssh key is required")), pathPrivateKeyValidationRemember)
                    }
                    if(isFormValid) {
                        if(nameRemember.value.isNotEmpty()) localRepository.name = nameRemember.value
                        localRepository.credentialType = if(state.value == 0) { HTTP.value } else { SSH.value }
                        if(state.value == 0) {
                            if (usernameRemember.value.isNotEmpty()) localRepository.username = usernameRemember.value
                            if (passwordRemember.value.isNotEmpty()) localRepository.password = passwordRemember.value
                            localRepository.pathKey = emptyString()
                            localRepository.passwordKey = emptyString()
                            localRepository.host = emptyString()
                        } else {
                            if(pathPrivateKeyRemember.value.isNotEmpty()) localRepository.pathKey = pathPrivateKeyRemember.value
                            if(passwordKeyRemember.value.isNotEmpty()) localRepository.passwordKey = passwordKeyRemember.value
                            if(sshHostRemember.value.isNotEmpty()) localRepository.host = sshHostRemember.value
                            localRepository.username = emptyString()
                            localRepository.password = emptyString()
                        }
                        editLocalRepositoryViewModel.update(localRepository)
                        close()
                    }
                }
            }
        }
    }
}