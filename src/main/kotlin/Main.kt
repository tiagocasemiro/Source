import Application.Screen.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.source.model.domain.LocalRepository
import br.com.source.modulesApp
import br.com.source.view.common.createSnackBar
import br.com.source.view.common.Load
import br.com.source.view.components.createDialog
import br.com.source.view.dashboard.Dashboard
import br.com.source.view.repositories.all.allRepository
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun main()  {
    startKoin {
        modules(listOf(modulesApp))
    }
    Application().start()
}


class Application : KoinComponent {
    private sealed class Screen {
        object AllRepositories : Screen()
        data class DashboardRepository(val localRepository: LocalRepository) : Screen()
    }
    private var titleWindow = mutableStateOf("Source")

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    fun start() = application {
        var isOpen by remember { mutableStateOf(true) }
        if (isOpen) {
            Window(
                onCloseRequest = {
                    isOpen = false
                },
                title = titleWindow.value,
                state = rememberWindowState(width = 1280.dp, height = 750.dp)
            ) {
                MaterialTheme {
                    Load {
                        rote(AllRepositories)
                    }
                    createDialog()
                    createSnackBar()
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun rote(initialScreen: Screen) {
        var screenState by remember { mutableStateOf(initialScreen) }
        when (val screen = screenState) {
            is AllRepositories -> {
                val title = "Source"
                if( titleWindow.value == title) {
                    allRepository(
                        openRepository = {
                            screenState = DashboardRepository(it)
                        },
                    )
                } else {
                    titleWindow.value = title
                }
            }
            is DashboardRepository -> {
                val titleFromRepo = "${screen.localRepository.name} - ${screen.localRepository.workDir}"
                if(titleWindow.value == titleFromRepo) {
                    Dashboard(
                        localRepository = screen.localRepository,
                        close = {
                            screenState = AllRepositories
                        }
                    )
                } else {
                    titleWindow.value = titleFromRepo
                }
            }
        }
    }
}

// jgit - https://github.com/centic9/jgit-cookbook
// figma - https://www.figma.com/file/tQzuFqj8D3CLdBOpYWVxEE/Source?node-id=497%3A2
// graph - https://github.com/Schachte/Java-Simple-Graph
// jetpack - https://blog.jetbrains.com/kotlin/2021/08/compose-multiplatform-goes-alpha/
// github - https://github.com/JetBrains/compose-jb
// examples - https://foso.github.io/Jetpack-Compose-Playground/material/badgedbox/#see-also
// canvas - https://developer.android.com/jetpack/compose/graphics?hl=pt-br