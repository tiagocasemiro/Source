package br.com.source

import br.com.source.model.database.LocalRepositoryDatabase
import br.com.source.model.git.Executor
import br.com.source.viewmodel.AddRepositoryViewModel
import br.com.source.viewmodel.AllRepositoriesViewModel
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.koin.dsl.module
import java.io.File

var modulesApp = module {
    factory {
        AllRepositoriesViewModel(get(), get())
    }
    factory {
        Executor(get())
    }
    single {
        LocalRepositoryDatabase()
    }
    factory {
        AddRepositoryViewModel(get(), get())
    }
    factory {
        val existingRepo: Repository = FileRepositoryBuilder()
            .setGitDir(File("/home/tiagocasemiro/Documentos/project/documentation/.git"))
            .build()

        Git(existingRepo)
    }
}