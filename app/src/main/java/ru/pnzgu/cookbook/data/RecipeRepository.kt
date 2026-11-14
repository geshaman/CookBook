package ru.pnzgu.cookbook.data

interface RecipeRepository {
    fun getRecipes(): List<Recipe>
}

class LocalRecipeRepository : RecipeRepository {
    override fun getRecipes(): List<Recipe> = sampleRecipes
}