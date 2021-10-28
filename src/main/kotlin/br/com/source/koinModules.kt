package br.com.source

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.service.GitService
import br.com.source.view.repositories.add.AddRepositoryViewModel
import br.com.source.view.repositories.all.AllRepositoriesViewModel
import org.eclipse.jgit.api.Git
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import java.io.File

var modulesApp = module {
    factory {
        AllRepositoriesViewModel(get())
    }
    factory { (repo: File) ->
        GitService(get { parametersOf(repo) })
    }
    single {
        LocalRepositoryDatabase()
    }
    factory {
        AddRepositoryViewModel(get())
    }
    factory { (repo: File) ->
        Git.open(repo)
    }
}