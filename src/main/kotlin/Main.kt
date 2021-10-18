// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import br.com.source.modulesApp
import br.com.source.view.selectRepository
import br.com.source.viewmodel.SelectRepositoryViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin



@Composable
@Preview
fun App() {
    val text by remember { mutableStateOf("Hello, World!") }

    Text(text)
}


fun main()  {
    startKoin {
        modules(listOf(modulesApp))
    }

   Application().start()
}

class Application : KoinComponent {
    private val selectRepositoryViewModel : SelectRepositoryViewModel by inject()

    fun start() = application {
        Window(onCloseRequest = ::exitApplication) {
            DesktopMaterialTheme {
                selectRepository(selectRepositoryViewModel)
            }
        }
    }
}


//https://github.com/centic9/jgit-cookbook
//https://www.figma.com/file/tQzuFqj8D3CLdBOpYWVxEE/Source?node-id=497%3A2


