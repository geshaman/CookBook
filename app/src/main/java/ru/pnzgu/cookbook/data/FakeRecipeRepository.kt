package ru.pnzgu.cookbook.data

import kotlinx.coroutines.delay

class FakeRecipeRepository : RecipeRepository {
    override suspend fun getRecipes(): List<Recipe> {
        delay(1500)
        return sampleRecipes
    }
    override suspend fun getRecipe(id: Int) = getRecipes().find { it.id == id }
}