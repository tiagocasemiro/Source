import Screen.AllRepositories
import Screen.DashboardRepository
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.source.model.domain.LocalRepository
import br.com.source.modulesApp
import br.com.source.view.allRepository
import br.com.source.view.dashboardRepository
import br.com.source.viewmodel.AllRepositoriesViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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
    private val allRepositoryViewModel: AllRepositoriesViewModel by inject()

    @ExperimentalMaterialApi
    @ExperimentalComposeUiApi
    fun start(initialScreen: Screen) = application {
        var isOpen by remember { mutableStateOf(true) }
        if (isOpen) {
            Window(
                onCloseRequest = {
                    isOpen = false
                },
                title = "Compose for Desktop",
                state = rememberWindowState(width = 900.dp, height = 600.dp)
            ) {
                DesktopMaterialTheme {
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
                allRepositoriesViewModel = allRepositoryViewModel,
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


//https://github.com/centic9/jgit-cookbook
//https://www.figma.com/file/tQzuFqj8D3CLdBOpYWVxEE/Source?node-id=497%3A2
