package ru.pnzgu.cookbook.ui.theme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import ru.pnzgu.cookbook.R
import ru.pnzgu.cookbook.data.Recipe
import ru.pnzgu.cookbook.ui.theme.common.snackbar

@Composable
fun RecipeScreen(
    recipe: Recipe,
    isAdmin: Boolean,
    isFavorite: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onStartCooking: () -> Unit,
    onRecipeDeleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var checkedIngredients by rememberSaveable { mutableStateOf(setOf<Int>()) }

    val placeholder = MaterialTheme.snackbar

    val allChecked = checkedIngredients.size == recipe.ingredients.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Картинка + кнопка редактирования для админа
        Card(modifier = Modifier.fillMaxWidth()) {
            Box {
                Image(
                    painter = painterResource(recipe.imageRes),
                    contentDescription = stringResource(recipe.titleRes),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    androidx.compose.animation.AnimatedVisibility(visible = isAdmin) {
                        FilledTonalIconButton(onClick = placeholder::notifyPlaceholder) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Чипсы времени и сложности, поделиться, избранное
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssistChip(
                onClick = placeholder::notifyPlaceholder,
                label = { Text(stringResource(R.string.cooking_time_minutes, recipe.cookingMinutes)) },
                leadingIcon = { Icon(Icons.Filled.Timer, contentDescription = null) }
            )
            Spacer(Modifier.width(8.dp))
            AssistChip(
                onClick = placeholder::notifyPlaceholder,
                label = { Text(stringResource(R.string.difficulty_easy)) }
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = placeholder::notifyPlaceholder) {
                Icon(
                    Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
            IconToggleButton(checked = isFavorite, onCheckedChange = onFavoriteChange) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite_toggle),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Описание
        SectionHeader(
            title = stringResource(R.string.section_description),
            isAdmin = isAdmin,
            onEdit = placeholder::notifyPlaceholder
        )
        Text(
            text = stringResource(recipe.descriptionRes),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // Ингредиенты (чеклист)
        SectionHeader(
            title = stringResource(R.string.section_ingredients),
            isAdmin = isAdmin,
            onEdit = placeholder::notifyPlaceholder
        )

        Spacer(Modifier.height(8.dp))

        recipe.ingredients.forEachIndexed { index, res ->
            val checked = index in checkedIngredients
            val toggle = {
                checkedIngredients =
                    if (checked) checkedIngredients - index else checkedIngredients + index
            }
            ListItem(
                modifier = Modifier.clickable { toggle() },
                headlineContent = {
                    Text(
                        text = stringResource(res),
                        textDecoration = if (checked) TextDecoration.LineThrough else null
                    )
                },
                leadingContent = {
                    Checkbox(checked = checked, onCheckedChange = { toggle() })
                }
            )
        }

        // Кнопка «Начать готовить» (когда отмечены все ингредиенты)
        AnimatedVisibility(visible = allChecked) {
            Button(
                onClick = onStartCooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(stringResource(R.string.start_cooking))
            }
        }

        // Удаление (только админ)
        AnimatedVisibility(visible = isAdmin) {
            Column {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.delete_recipe))
                }
            }
        }
    }

    // Диалог подтверждения удаления
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = { Text(stringResource(R.string.delete_dialog_text)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    placeholder.notifyPlaceholder()
                    onRecipeDeleted()
                }) {
                    Text(
                        text = stringResource(R.string.confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/** Заголовок блока; иконка редактирования видна только админу. */
@Composable
private fun SectionHeader(title: String, isAdmin: Boolean, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.weight(1f))
        AnimatedVisibility(visible = isAdmin) {
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
            }
        }
    }
}