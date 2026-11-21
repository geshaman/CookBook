package ru.pnzgu.cookbook.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pnzgu.cookbook.data.NetworkRecipeRepository
import ru.pnzgu.cookbook.data.Recipe
import ru.pnzgu.cookbook.data.RecipeRepository

data class AppUiState(
    val recipes: List<Recipe> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isAdmin: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

class RecipesViewModel(
    private val repository: RecipeRepository = NetworkRecipeRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val recipes = repository.getRecipes()
                _state.update { it.copy(recipes = recipes, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error") }
            }
        }
    }

    fun toggleFavorite(id: Int, fav: Boolean) = _state.update {
        it.copy(favoriteIds = if (fav) it.favoriteIds + id else it.favoriteIds - id)
    }

    fun setAdmin(value: Boolean) = _state.update { it.copy(isAdmin = value) }
}