package ru.pnzgu.cookbook.data

import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeApi {
    @GET("recipes")
    suspend fun getRecipes(): List<Recipe>

    @GET("recipes/{id}")
    suspend fun getRecipe(@Path("id") id: Int): Recipe
}

