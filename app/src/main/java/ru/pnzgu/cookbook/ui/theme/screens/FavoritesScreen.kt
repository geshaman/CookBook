package ru.pnzgu.cookbook.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.pnzgu.cookbook.R

@Composable
fun FavoritesScreen(
    isFavorite: Boolean,
    onRecipeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column (modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (isFavorite) {
            RecipeCard(onClick = onRecipeClick)
        } else {
            Text(stringResource(R.string.favorites_empty), style = MaterialTheme.typography.bodyLarge)
        }
    }
}