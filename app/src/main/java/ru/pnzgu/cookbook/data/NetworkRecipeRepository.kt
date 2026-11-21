package ru.pnzgu.cookbook.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRecipeRepository : RecipeRepository {
    private val api: RecipeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApi::class.java)
    }

    override suspend fun getRecipes(): List<Recipe> = api.getRecipes()

    override suspend fun getRecipe(id: Int): Recipe = api.getRecipe(id)

    companion object {
        const val BASE_URL = "https://590bc91fcec06d.app.lhr.life/"
    }
}