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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.pnzgu.cookbook.ui.theme.common.LocalPlaceholderNotifier
import ru.pnzgu.cookbook.ui.theme.common.PlaceholderNotifier
import ru.pnzgu.cookbook.ui.theme.common.showNotImplemented
import ru.pnzgu.cookbook.ui.theme.screens.CookingScreen
import ru.pnzgu.cookbook.ui.theme.screens.RecipeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.pnzgu.cookbook.ui.theme.screens.HomeScreen
import ru.pnzgu.cookbook.ui.theme.screens.FavoritesScreen
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
    const val FAVORITES = "favorites"
    const val RECIPE = "recipe"
    const val COOKING = "cooking"
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
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

    var isFavorite by rememberSaveable { mutableStateOf(false) }

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
                                    else -> R.string.recipe_title
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
                        selected = currentRoute != Routes.FAVORITES,
                        onClick = {
                            if (!navController.popBackStack(Routes.HOME, inclusive = false)) {
                                navController.navigateTopLevel(Routes.HOME)
                            }
                          },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.FAVORITES,
                        onClick = { navController.navigateTopLevel(Routes.FAVORITES) },
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_favorites)) }
                    )
                }
            }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.HOME) {
                HomeScreen(onRecipeClick = { navController.navigate(Routes.RECIPE) })
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen(
                    isFavorite = isFavorite,
                    onRecipeClick = { navController.navigate(Routes.RECIPE) }
                )
            }
            composable(Routes.RECIPE) {
                RecipeScreen(
                    isAdmin = isAdmin,
                    isFavorite = isFavorite,
                    onFavoriteChange = { isFavorite = it },
                    onStartCooking = { navController.navigate(Routes.COOKING)},
                    onRecipeDeleted = {navController.popBackStack(Routes.HOME, inclusive = false) }
                )
            }
            composable(Routes.COOKING) {
                CookingScreen()
            }
        }
    }
    }
}

