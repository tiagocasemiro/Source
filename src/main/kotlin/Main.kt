import Screen.AllRepositories
import Screen.DashboardRepository
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import br.com.source.model.domain.LocalRepository
import br.com.source.modulesApp
import br.com.source.view.addLocalRepository
import br.com.source.view.allRepository
import br.com.source.view.dashboardRepository
import br.com.source.viewmodel.AllRepositoriesViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin

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

    @ExperimentalComposeUiApi
    fun start(initialScreen: Screen) = application {
        var isOpen by remember { mutableStateOf(true) }
        if (isOpen) {
            Window(
                onCloseRequest = {
                    isOpen = false
                },
                title = "Compose for Desktop",
                state = rememberWindowState(width = 800.dp, height = 600.dp)
            ) {
                menu(this) {
                    isOpen = false
                }
                DesktopMaterialTheme {
                    rote(initialScreen)
                }
            }
        }
    }

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

    @ExperimentalComposeUiApi
    @Composable
    private fun menu(scope: FrameWindowScope, close: () -> Unit) {
        var isLocalRepositoryDialogOpen by remember { mutableStateOf(false) }
        if(isLocalRepositoryDialogOpen) {
           addLocalRepositoryDialog {
               isLocalRepositoryDialogOpen = false
           }
        }
        scope.MenuBar {
            Menu("File", mnemonic = 'F') {
                Menu("New repository") {
                    Item("Local", mnemonic = 'L', onClick = {
                        isLocalRepositoryDialogOpen = true
                    }, shortcut = KeyShortcut(Key.L, ctrl = true))
                    Item("Remote",  mnemonic = 'R', onClick = {
                        // todo implement
                    }, shortcut = KeyShortcut(Key.R, ctrl = true))
                }
                Separator()
                Item("Exit", mnemonic = 'E', onClick = {
                    close()
                })
            }
            Menu("Help") {
                Item("About",  mnemonic = 'A',onClick = {
                    // todo implement
                })
            }
        }
    }
}

@Composable
fun addLocalRepositoryDialog(close: () -> Unit) {
    Dialog(
        onCloseRequest = {
            close()
        },
        title = "Add new local repository",
    ) {
        addLocalRepository(close)
    }
}

//https://github.com/centic9/jgit-cookbook
//https://www.figma.com/file/tQzuFqj8D3CLdBOpYWVxEE/Source?node-id=497%3A2
