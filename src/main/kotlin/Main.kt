import Application.Screen.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.source.model.domain.LocalRepository
import br.com.source.modulesApp
import br.com.source.view.common.CreateNotification
import br.com.source.view.common.Load
import br.com.source.view.components.createDialog
import br.com.source.view.dashboard.Dashboard
import br.com.source.view.repositories.all.allRepository
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin
import kotlin.system.exitProcess

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun main()  {
    startKoin {
        modules(listOf(modulesApp))
    }
    Application().start()
}


class Application : KoinComponent {
    private sealed class Screen(val title: String) {
        object AllRepositories : Screen("Source")
        data class DashboardRepository(val localRepository: LocalRepository) : Screen("${localRepository.name} - ${localRepository.workDir}")
        object  Close: Screen("Source")
    }

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    fun start() = application {
        val screenState = remember { mutableStateOf<Screen>(AllRepositories) }
        if (screenState.value != Close) {
            Window(
                onCloseRequest = {
                    screenState.value = Close
                },
                title = screenState.value.title,
                state = rememberWindowState(width = 1280.dp, height = 750.dp),
                icon = BitmapPainter(useResource("source-launch-icon.png", ::loadImageBitmap)),
            ) {
                MaterialTheme {
                    Load {
                        CreateNotification {
                            rote(screenState.value) {
                                screenState.value = it
                            }
                        }
                    }
                    createDialog()
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun rote(screen: Screen, changeScreen: (Screen) -> Unit) {
        when (screen) {
            is AllRepositories -> {
                allRepository(
                    openRepository = {
                        changeScreen(DashboardRepository(it))
                    },
                )
            }
            is DashboardRepository -> {
                Dashboard(
                    localRepository = screen.localRepository,
                    close = {
                        changeScreen(AllRepositories)
                    }
                )
            }
            else -> {
                exitProcess(0)
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