package ru.pnzgu.cookbook.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Recipe(
    val id: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int,
    val cookingMinutes: Int,
    val ingredients: List<Int>,   // id строк
    val steps: List<Step>
)