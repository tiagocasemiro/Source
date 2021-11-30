package br.com.source.view.dashboard.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.source.view.common.dialogBackgroundColor

@Composable
fun LogoContainer() {
    Box(
        modifier = Modifier.fillMaxSize().background(dialogBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/source-logo.svg"),
            contentDescription = "Source app logo",
            modifier = Modifier.height(45.dp)
        )
    }
}