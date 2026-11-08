package ru.pnzgu.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import ru.pnzgu.cookbook.ui.theme.CookBookTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.pnzgu.cookbook.ui.theme.common.LocalPlaceholderNotifier
import ru.pnzgu.cookbook.ui.theme.common.PlaceholderNotifier
import ru.pnzgu.cookbook.ui.theme.common.showNotImplemented
import ru.pnzgu.cookbook.ui.theme.screens.CookingScreen
import ru.pnzgu.cookbook.ui.theme.screens.RecipeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ru.pnzgu.cookbook.ui.theme.screens.HomeScreen
import ru.pnzgu.cookbook.ui.theme.screens.FavoritesScreen
import ru.pnzgu.cookbook.data.sampleRecipes
import ru.pnzgu.cookbook.data.findRecipe
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CookBookTheme {
                CookBookApp()
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val HOME_GRAPH = "home_graph"
    const val FAVORITES = "favorites"
    const val FAVORITES_GRAPH = "favorites_graph"
    const val RECIPE = "recipe/{recipeId}"
    const val COOKING = "cooking/{recipeId}"

}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavGraphBuilder.recipeDestinations(
    prefix: String,
    rootRoute: String,
    navController: NavHostController,
    isAdmin: Boolean,
    favoriteIds: Set<Int>,
    onFavoriteChange: (Int, Boolean) -> Unit
) {
    composable(
        route = "$prefix/${Routes.RECIPE}",
        arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
    ) { entry ->
        val recipe = findRecipe(entry.arguments?.getInt("recipeId") ?: -1)
            ?: return@composable
        RecipeScreen(
            recipe = recipe,
            isAdmin = isAdmin,
            isFavorite = recipe.id in favoriteIds,
            onFavoriteChange = { onFavoriteChange(recipe.id, it) },
            onStartCooking = { navController.navigate("$prefix/cooking/${recipe.id}") },
            onRecipeDeleted = { navController.popBackStack(rootRoute, inclusive = false) }
        )
    }
    composable(
        route = "$prefix/${Routes.COOKING}",
        arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
    ) { entry ->
        val recipe = findRecipe(entry.arguments?.getInt("recipeId") ?: -1)
            ?: return@composable
        CookingScreen(recipe = recipe)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookBookApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val message = stringResource(R.string.notice_not_implemented)
    val notifier = remember(message, snackbarHostState) {
        PlaceholderNotifier {
            scope.showNotImplemented(
                snackbarHostState,
                message
            )
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val destination = backStackEntry?.destination
    val inFavorites = destination?.hierarchy?.any { it.route == Routes.FAVORITES_GRAPH } == true
    val inHome = !inFavorites

    var favoriteIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val onFavoriteChange = { id: Int, fav: Boolean ->
        favoriteIds = if (fav) favoriteIds + id else favoriteIds - id
    }

    var isAdmin by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    CompositionLocalProvider(LocalPlaceholderNotifier provides notifier) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                AnimatedVisibility(visible = isAdmin && currentRoute == Routes.HOME) {
                    ExtendedFloatingActionButton(
                        onClick = notifier::notifyPlaceholder,
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        text = { Text(stringResource(R.string.add_recipe)) }
                    )
                }
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(
                                when (currentRoute) {
                                    Routes.HOME -> R.string.app_name
                                    Routes.FAVORITES -> R.string.nav_favorites
                                    else -> R.string.default_recipe_title
                                }
                            )
                        )
                    },
                    actions = {
                        if (currentRoute != Routes.FAVORITES) {
                            Text(stringResource(R.string.role_admin))
                            Spacer(Modifier.width(8.dp))
                            Switch(checked = isAdmin, onCheckedChange = { isAdmin = it })
                            Spacer(Modifier.width(8.dp))
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = inHome,
                        onClick = {
                            if (inHome) navController.popBackStack(Routes.HOME, inclusive = false)
                            else navController.navigateTopLevel(Routes.HOME_GRAPH)
                        },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = inFavorites,
                        onClick = {
                            if (inFavorites) navController.popBackStack(Routes.FAVORITES, inclusive = false)
                            else navController.navigateTopLevel(Routes.FAVORITES_GRAPH)
                        },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_favorites)) }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME_GRAPH,
                modifier = Modifier.padding(paddingValues)
            ) {
                navigation(startDestination = Routes.HOME, route = Routes.HOME_GRAPH) {
                    composable(Routes.HOME) {
                        HomeScreen(
                            recipes = sampleRecipes,
                            onRecipeClick = { id -> navController.navigate("${Routes.HOME_GRAPH}/recipe/$id") }
                        )
                    }
                    recipeDestinations(Routes.HOME_GRAPH, Routes.HOME, navController, isAdmin, favoriteIds, onFavoriteChange)
                }
                navigation(startDestination = Routes.FAVORITES, route = Routes.FAVORITES_GRAPH) {
                    composable(Routes.FAVORITES) {
                        FavoritesScreen(
                            recipes = sampleRecipes.filter { it.id in favoriteIds },
                            onRecipeClick = { id -> navController.navigate("${Routes.FAVORITES_GRAPH}/recipe/$id") }
                        )
                    }
                    recipeDestinations(Routes.FAVORITES_GRAPH, Routes.FAVORITES, navController, isAdmin, favoriteIds, onFavoriteChange)
                }
            }
        }
    }
}

