package ru.pnzgu.cookbook.data

import ru.pnzgu.cookbook.R

val sampleRecipes = listOf(
    Recipe(
        id = 1,
        titleRes = R.string.recipe_title,
        descriptionRes = R.string.recipe_description,
        imageRes = R.drawable.pancakes,
        cookingMinutes = 40,
        ingredients = listOf(R.string.ingredient_flour, R.string.ingredient_milk /* ... */),
        steps = listOf(Step(R.string.step_1), Step(R.string.step_2, 120), Step(R.string.step_3))
    ),
    // Recipe(id = 2, ...) для второго блюда
)

fun findRecipe(id: Int) = sampleRecipes.find { it.id == id }