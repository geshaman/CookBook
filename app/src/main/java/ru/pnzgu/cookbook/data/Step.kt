package ru.pnzgu.cookbook.data

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val text: String,
    val timerSeconds: Int? = null
)
