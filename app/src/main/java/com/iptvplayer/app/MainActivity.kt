package com.iptvplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.iptvplayer.app.data.Channel
import com.iptvplayer.app.data.ContentType
import com.iptvplayer.app.ui.AppViewModel
import com.iptvplayer.app.ui.screens.HomeScreen
import com.iptvplayer.app.ui.screens.ManageCategoriesScreen
import com.iptvplayer.app.ui.screens.ManageChannelsScreen
import com.iptvplayer.app.ui.screens.PlayerScreen
import com.iptvplayer.app.ui.screens.SettingsScreen
import com.iptvplayer.app.ui.theme.NextAIPtvPlayerTheme

private sealed class Screen {
    data object Home : Screen()
    data object Settings : Screen()
    data class ManageCategories(val type: ContentType) : Screen()
    data class ManageChannels(val type: ContentType, val categoryName: String) : Screen()
    data class Player(val channel: Channel) : Screen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NextAIPtvPlayerTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
private fun AppNavigation(viewModel: AppViewModel) {
    var backStack by remember { mutableStateOf<List<Screen>>(listOf(Screen.Home)) }
    val current = backStack.last()

    fun push(screen: Screen) {
        backStack = backStack + screen
    }

    fun pop() {
        if (backStack.size > 1) backStack = backStack.dropLast(1)
    }

    BackHandler(enabled = backStack.size > 1) { pop() }

    when (val screen = current) {
        is Screen.Home -> HomeScreen(
            viewModel = viewModel,
            onOpenSettings = { push(Screen.Settings) },
            onPlay = { channel -> push(Screen.Player(channel)) }
        )

        is Screen.Settings -> SettingsScreen(
            onBack = { pop() },
            onChangePlaylist = {
                // Volvemos al inicio: si no hay lista cargada, ahí se puede introducir una nueva URL.
                backStack = listOf(Screen.Home)
            },
            onManageCategories = { type -> push(Screen.ManageCategories(type)) }
        )

        is Screen.ManageCategories -> ManageCategoriesScreen(
            viewModel = viewModel,
            initialType = screen.type,
            onBack = { pop() },
            onOpenChannels = { type, name -> push(Screen.ManageChannels(type, name)) }
        )

        is Screen.ManageChannels -> ManageChannelsScreen(
            viewModel = viewModel,
            type = screen.type,
            categoryName = screen.categoryName,
            onBack = { pop() }
        )

        is Screen.Player -> PlayerScreen(
            channel = screen.channel,
            onBack = { pop() }
        )
    }
}
