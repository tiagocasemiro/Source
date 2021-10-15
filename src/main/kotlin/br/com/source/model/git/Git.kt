package br.com.source.model.git

import br.com.source.model.process.runCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File

private val existingRepo: Repository = FileRepositoryBuilder()
  .setGitDir(File("/home/tiagocasemiro/Documentos/project/documentation/.git"))
  .build()

val git = Git(existingRepo)

fun Git.fullStatus(): String {
  return runCommand("git status", git.repository.workTree)
}