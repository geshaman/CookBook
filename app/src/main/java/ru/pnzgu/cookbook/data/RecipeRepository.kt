package ru.pnzgu.cookbook.data

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun getRecipe(id: Int): Recipe?
}