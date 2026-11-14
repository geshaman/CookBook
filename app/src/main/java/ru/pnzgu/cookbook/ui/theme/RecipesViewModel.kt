package ru.pnzgu.cookbook.ui.theme

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.pnzgu.cookbook.data.LocalRecipeRepository
import ru.pnzgu.cookbook.data.Recipe
import ru.pnzgu.cookbook.data.RecipeRepository

data class AppUiState(
    val recipes: List<Recipe> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isAdmin: Boolean = false
)

class RecipesViewModel (repo: RecipeRepository = LocalRecipeRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(AppUiState(recipes = repo.getRecipes()))
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    fun toggleFavorite(id: Int, fav: Boolean) = _state.update {
        it.copy(favoriteIds = if (fav) it.favoriteIds + id else it.favoriteIds - id)
    }
    fun setAdmin(value: Boolean) = _state.update { it.copy(isAdmin = value) }
}