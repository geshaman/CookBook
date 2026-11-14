package ru.pnzgu.cookbook.data

import ru.pnzgu.cookbook.R

val sampleRecipes = listOf(
    Recipe(
        id = 1,
        titleRes = R.string.pancake_title,
        descriptionRes = R.string.pancake_description,
        imageRes = R.drawable.pancakes,
        cookingMinutes = 40,
        ingredients = listOf(R.string.pancake_ingredient_eggs, R.string.pancake_ingredient_cheese,
            R.string.pancake_ingredient_flour, R.string.pancake_ingredient_milk),
        steps = listOf(Step(R.string.pancake_step_1),
            Step(R.string.pancake_step_2, 120),
            Step(R.string.pancake_step_3))
    ),
    Recipe(
        id = 2,
        titleRes = R.string.bolognese_title,
        descriptionRes = R.string.bolognese_description,
        imageRes = R.drawable.bolon,
        cookingMinutes = 40,
        ingredients = listOf(
            R.string.bolognese_ingredient_onion, R.string.bolognese_ingredient_garlic,
            R.string.bolognese_ingredient_meat, R.string.bolognese_ingredient_cheese,
            R.string.bolognese_ingredient_olive_oil, R.string.bolognese_ingredient_spaghetti,
            R.string.bolognese_ingredient_sweet_pepper, R.string.bolognese_ingredient_tomatoes),
        steps = listOf(
            Step(R.string.bolognese_step_1), Step(R.string.bolognese_step_2),
            Step(R.string.bolognese_step_3), Step(R.string.bolognese_step_4),
            Step(R.string.bolognese_step_5), Step(R.string.bolognese_step_6),
            Step(R.string.bolognese_step_7), Step(R.string.bolognese_step_8),)
    ),
    Recipe(
        id = 3,
        titleRes = R.string.omelet_title,
        descriptionRes = R.string.omelet_description,
        imageRes = R.drawable.omelet,
        cookingMinutes = 10,
        ingredients = listOf(
            R.string.omelet_ing_eggs,
            R.string.omelet_ing_milk,
            R.string.omelet_ing_milk,
            R.string.omelet_ing_cheese,
            R.string.omelet_ing_butter
        ),
        steps = listOf(
            Step(R.string.omelet_step_1),
            Step(R.string.omelet_step_2),
            Step(R.string.omelet_step_3, timerSeconds = 180),
            Step(R.string.omelet_step_4)
        )
    )
)

fun findRecipe(id: Int) = sampleRecipes.find { it.id == id }