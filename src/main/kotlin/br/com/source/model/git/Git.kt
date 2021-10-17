package br.com.source.model.git

import br.com.source.model.process.runCommand
import org.eclipse.jgit.api.Git

class Executor(private val git: Git) {

  fun fullStatus(): String {
    return runCommand("git status", git.repository.workTree)
  }
}
