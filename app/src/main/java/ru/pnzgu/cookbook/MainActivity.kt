package ru.pnzgu.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.unit.dp

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookBookApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val message = stringResource(R.string.notice_not_implemented)
    val notifier = remember(message, snackbarHostState) {
        PlaceholderNotifier { scope.showNotImplemented(snackbarHostState,
            message) }
    }

    var isAdmin by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    CompositionLocalProvider(LocalPlaceholderNotifier provides notifier) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.recipe_title)) },
                    actions = {
                        Text(stringResource(R.string.role_admin))
                        Spacer(Modifier.width(8.dp))
                        Switch(checked = isAdmin, onCheckedChange = { isAdmin = it })
                        Spacer(Modifier.width(8.dp))
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = MaterialTheme.snackbar::notifyPlaceholder,
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = MaterialTheme.snackbar::notifyPlaceholder,
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_favorites)) }
                    )
                }
            }
        ) { paddingValues ->
            RecipeScreen(
                isAdmin = isAdmin,
                modifier = Modifier.padding(paddingValues))
        }
    }
}

