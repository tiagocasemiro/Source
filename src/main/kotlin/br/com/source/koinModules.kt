package br.com.source

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.view.repositories.add.AddRepositoryViewModel
import br.com.source.view.repositories.all.AllRepositoriesViewModel
import br.com.source.view.repositories.edit.EditRepositoryViewModel
import org.koin.dsl.module

var modulesApp = module {
    factory {
        AllRepositoriesViewModel(get())
    }
    single {
        LocalRepositoryDatabase()
    }
    factory {
        AddRepositoryViewModel(get())
    }
    factory {
        EditRepositoryViewModel(get())
    }
}