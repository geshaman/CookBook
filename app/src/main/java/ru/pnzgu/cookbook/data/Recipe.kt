package ru.pnzgu.cookbook.data

data class Recipe(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val cookingMinutes: Int,
    val ingredients: List<String>,
    val steps: List<Step>
)