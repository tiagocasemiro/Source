package br.com.source

import br.com.source.model.git.Executor
import br.com.source.viewmodel.SelectRepositoryViewModel
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.koin.dsl.module
import java.io.File

var modulesApp = module {
    factory {
        SelectRepositoryViewModel(get())
    }

    factory {
        Executor(get())
    }

    factory {
        val existingRepo: Repository = FileRepositoryBuilder()
            .setGitDir(File("/home/tiagocasemiro/Documentos/project/documentation/.git"))
            .build()

        Git(existingRepo)
    }
}