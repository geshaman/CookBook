package ru.pnzgu.cookbook.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import ru.pnzgu.cookbook.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(onRecipeClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        RecipeCard(onClick = onRecipeClick)
    }
}

@Composable
fun RecipeCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card (onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(R.drawable.pancakes),
            contentDescription = stringResource(R.string.recipe_image_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.recipe_title), style = MaterialTheme.typography.titleLarge)
            Text(
                stringResource(R.string.cooking_time_minutes, 40),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}