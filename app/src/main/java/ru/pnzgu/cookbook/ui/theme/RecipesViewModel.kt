package ru.pnzgu.cookbook.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.pnzgu.cookbook.data.FakeRecipeRepository
import ru.pnzgu.cookbook.data.RecipeRepository
import ru.pnzgu.cookbook.data.Recipe

sealed interface RecipesUiState {
    data object Loading : RecipesUiState
    data class Success(val recipes: List<Recipe>) : RecipesUiState
    data class Error(val message: String) : RecipesUiState
}

class RecipesViewModel (private val repository: RecipeRepository = FakeRecipeRepository())
    : ViewModel() {
    private val _state = MutableStateFlow<RecipesUiState>(RecipesUiState.Loading)
    val state: StateFlow<RecipesUiState> = _state.asStateFlow()

    private val _favoriteIds = MutableStateFlow(setOf<Int>())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = RecipesUiState.Loading
            _state.value = try {
                RecipesUiState.Success(repository.getRecipes())
            } catch (e: Exception) {
                RecipesUiState.Error(e.message ?: "Error")
            }
        }
    }

    fun toggleFavorite(id: Int, fav: Boolean) {
        _favoriteIds.value = if (fav) _favoriteIds.value + id else _favoriteIds.value - id
    }

    fun setAdmin(value: Boolean) { _isAdmin.value = value }
}
