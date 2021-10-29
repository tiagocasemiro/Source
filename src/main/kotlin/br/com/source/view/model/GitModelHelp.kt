package br.com.source.view.model

import br.com.source.model.util.emptyString

data class Branch(
    val isCurrent: Boolean = false,
    val fullName: String,
) {
    private val clearName = fullName.replaceFirst("refs/heads/", emptyString())
        .replaceFirst("refs/remotes/origin/", emptyString())
    val name: String = fullName.split("/").last()
    val folder: String = clearName
        .split("/")
        .toMutableList()
        .apply { removeLast() }.toString()
        .replace("[", emptyString())
        .replace("]", emptyString())

    fun hasFolder(): Boolean {
        return clearName.contains("/")
    }

    override fun toString(): String {
        return "Branch(isCurrent=$isCurrent, fullName='$fullName', name='$name', folder='$folder')"
    }
}

data class Tag(
    val name: String
)

data class Stash(
    val name: String
)