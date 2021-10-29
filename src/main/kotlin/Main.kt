import Screen.AllRepositories
import Screen.DashboardRepository
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.source.model.domain.LocalRepository
import br.com.source.modulesApp
import br.com.source.view.repositories.all.allRepository
import br.com.source.view.dashboard.dashboardRepository
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
fun main()  {
    startKoin {
        modules(listOf(modulesApp))
    }
    Application().start(AllRepositories)
}

sealed class Screen {
    object AllRepositories : Screen()
    data class DashboardRepository(val localRepository: LocalRepository) : Screen()
}

class Application : KoinComponent {

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    fun start(initialScreen: Screen) = application {
        var isOpen by remember { mutableStateOf(true) }
        if (isOpen) {
            Window(
                onCloseRequest = {
                    isOpen = false
                },
                title = "Source",
                state = rememberWindowState(width = 1280.dp, height = 750.dp)
            ) {
                MaterialTheme {
                    rote(initialScreen)
                }
            }
        }
    }

    @ExperimentalMaterialApi
    @Composable
    private fun rote(initialScreen: Screen) {
        var screenState by remember { mutableStateOf(initialScreen) }
        when (val screen = screenState) {
            is AllRepositories -> allRepository(
                openRepository = {
                    screenState = DashboardRepository(it)
                }
            )
            is DashboardRepository -> dashboardRepository(
                localRepository = screen.localRepository,
                close = { screenState = AllRepositories }
            )
        }
    }
}


// jgit - https://github.com/centic9/jgit-cookbook
// figma - https://www.figma.com/file/tQzuFqj8D3CLdBOpYWVxEE/Source?node-id=497%3A2
// graph - https://github.com/Schachte/Java-Simple-Graph
// jetpack - https://blog.jetbrains.com/kotlin/2021/08/compose-multiplatform-goes-alpha/