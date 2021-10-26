package br.com.source.view.dashboard.logo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun LogoContainer() {
    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource("images/source-logo.svg"),
            contentDescription = "Source app logo",
            modifier = Modifier.height(45.dp)
        )
    }
}