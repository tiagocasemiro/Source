package br.com.source.view.dashboard.left.branches

import br.com.source.view.model.Branch

class BranchesViewModel {

    fun localBranches(): List<Branch> {
        return listOf(Branch(name = "TSG-5656"), Branch(name = "TJH-7456"), Branch(name = "TBS-5126"))
    }

    fun remoteBranches(): List<Branch> {
        return listOf(Branch(name = "TSG-5656"), Branch(name = "TJH-7456"), Branch(name = "TBS-5126"))
    }

    fun tags(): List<String> {
        return listOf("release-01", "dev-254", "release-02", "release-03")
    }

    fun stashs(): List<String> {
        return listOf("Adicao de icone", "SUP-8547", "logo com margin 10")
    }
}