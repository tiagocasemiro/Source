package br.com.source.view.model

data class Branch(
    val isCurrent: Boolean = false,
    val name: String,
    val folder: String? = null
)

data class Tag(
    val name: String
)

data class Stash(
    val name: String
)