package ru.pnzgu.cookbook

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun RecipeScreen(isAdmin: Boolean, modifier: Modifier = Modifier) {
    // --- Состояние экрана (переживает поворот) ---
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var doneSteps by rememberSaveable { mutableStateOf(setOf<Int>()) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var checkedIngredients by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val allChecked = checkedIngredients.size == 

    val placeholder = MaterialTheme.snackbar

    val tabs = listOf(R.string.tab_ingredients, R.string.tab_steps)
    val ingredients = listOf(
        R.string.ingredient_flour, R.string.ingredient_milk,
        R.string.ingredient_eggs, R.string.ingredient_cheese
    )
    val steps = listOf(R.string.step_1, R.string.step_2, R.string.step_3)


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. Картинка + кнопка редактирования для админа
        Card(modifier = Modifier.fillMaxWidth()) {
            Box {
                Image(
                    painter = painterResource(R.drawable.pancakes),
                    contentDescription = stringResource(R.string.recipe_image_desc),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    androidx.compose.animation.AnimatedVisibility(visible = isAdmin) {
                        FilledTonalIconButton(onClick = placeholder::notifyPlaceholder) {
                            Icon(Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit))
                        }
                    }
                }
            }

        }
        Spacer(Modifier.height(12.dp))
        // 2. Чипсы времени и сложности, избранное, поделиться
        Row(verticalAlignment = Alignment.
        CenterVertically) {
            AssistChip(
                onClick = placeholder::notifyPlaceholder,
                label = { Text(stringResource(R.string.cooking_time_minutes, 40)) },
                leadingIcon = { Icon(Icons.Filled.Timer, contentDescription = null) }
            )
            Spacer(Modifier.width(8.dp))
            AssistChip(
                onClick = placeholder::notifyPlaceholder,
                label = { Text(stringResource(R.string.difficulty_easy)) }
            )
            Spacer(Modifier.weight(1f))
            IconButton(onClick = placeholder::notifyPlaceholder) {
                Icon(Icons.Filled.Share,
                    contentDescription = stringResource(R.string.share))
            }
            IconToggleButton(checked = isFavorite, onCheckedChange = { isFavorite = it }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite_toggle),
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        // 3. Описание
        SectionHeader(
            title = stringResource(R.string.section_description),
            isAdmin = isAdmin,
            onEdit = placeholder::notifyPlaceholder
        )
        Text(
            text = stringResource(R.string.recipe_description),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        // 4. Вкладки
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, titleRes ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(stringResource(titleRes)) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 5. Содержимое вкладки
        SectionHeader(
            title = stringResource(tabs[selectedTab]),
            isAdmin = isAdmin,
            onEdit = placeholder::notifyPlaceholder
        )
        if (selectedTab == 0) {
            ingredients.forEach { res ->
                ListItem(headlineContent = { Text(stringResource(res)) })
            }
        } else {
            steps.forEachIndexed { index, res ->
                val done = index in doneSteps
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(res),
                            textDecoration = if (done) TextDecoration.LineThrough else null
                        )
                    },
                    leadingContent = {
                        Checkbox(
                            checked = done,
                            onCheckedChange = {
                                doneSteps = if (it) doneSteps + index else doneSteps - index
                            }
                        )
                    }
                )
            }
        }
        // 6. Удаление (только админ)
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
                    placeholder.notifyPlaceholder() // здесь должен быть возврат на главную
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
