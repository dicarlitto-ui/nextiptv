package com.iptvplayer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iptvplayer.app.data.Channel
import com.iptvplayer.app.data.ContentType
import com.iptvplayer.app.ui.AppViewModel
import com.iptvplayer.app.ui.components.ChannelLogo
import com.iptvplayer.app.ui.components.TvFocusable
import com.iptvplayer.app.ui.theme.AccentCyan
import com.iptvplayer.app.ui.theme.BackgroundDark
import com.iptvplayer.app.ui.theme.SurfaceDark
import com.iptvplayer.app.ui.theme.TextMuted
import com.iptvplayer.app.ui.theme.TextPrimary

fun ContentType.label(): String = when (this) {
    ContentType.LIVE -> "En vivo"
    ContentType.MOVIE -> "Películas"
    ContentType.SERIES -> "Series"
}

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onOpenSettings: () -> Unit,
    onPlay: (Channel) -> Unit
) {
    val categories by viewModel.visibleCategories.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.loadError.collectAsStateWithLifecycle()

    var selectedType by remember { mutableStateOf(ContentType.LIVE) }
    var selectedCategoryName by remember { mutableStateOf<String?>(null) }
    var urlInput by remember { mutableStateOf("") }
    var manualShowLoader by remember { mutableStateOf(false) }
    val showLoader = categories.isEmpty() || manualShowLoader

    val typeCategories = categories.filter { it.type == selectedType }
    val currentCategory = typeCategories.firstOrNull { it.name == selectedCategoryName } ?: typeCategories.firstOrNull()

    Column(
        Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(28.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "NextAIPtvPlayer",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (categories.isNotEmpty()) {
                    TvFocusable(onClick = { manualShowLoader = !manualShowLoader }) {
                        Text(
                            "Cambiar lista",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración", tint = TextMuted)
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (showLoader) {
            Text("Añade tu lista IPTV (M3U) para empezar", color = TextMuted)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("https://tuservidor.com/lista.m3u") }
                )
                Spacer(Modifier.width(12.dp))
                Button(onClick = {
                    if (urlInput.isNotBlank()) {
                        viewModel.loadPlaylist(urlInput.trim())
                        manualShowLoader = false
                    }
                }) {
                    Text("Cargar")
                }
            }
            error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ContentType.values().forEach { type ->
                    val count = categories.filter { it.type == type }.sumOf { it.channels.size }
                    TabChip(
                        label = type.label(),
                        count = count,
                        selected = type == selectedType,
                        onClick = {
                            selectedType = type
                            selectedCategoryName = null
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(Modifier.fillMaxSize()) {
                LazyColumn(Modifier.width(160.dp)) {
                    items(typeCategories) { category ->
                        SidebarItem(
                            name = category.name,
                            selected = category.name == (selectedCategoryName ?: currentCategory?.name),
                            onClick = { selectedCategoryName = category.name }
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 130.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(currentCategory?.channels ?: emptyList()) { channel ->
                        ChannelTile(channel = channel, onClick = { onPlay(channel) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TabChip(label: String, count: Int, selected: Boolean, onClick: () -> Unit) {
    TvFocusable(
        onClick = onClick,
        backgroundColor = if (selected) AccentCyan.copy(alpha = 0.12f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, color = if (selected) AccentCyan else TextPrimary, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            Text("$count canales", color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SidebarItem(name: String, selected: Boolean, onClick: () -> Unit) {
    TvFocusable(
        onClick = onClick,
        backgroundColor = if (selected) SurfaceDark else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            color = if (selected) TextPrimary else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun ChannelTile(channel: Channel, onClick: () -> Unit) {
    TvFocusable(onClick = onClick, modifier = Modifier.aspectRatio(1.6f)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.size(28.dp)) { ChannelLogo(name = channel.name, logoUrl = channel.logoUrl, size = 28.dp) }
            Text(
                text = channel.name,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
        }
    }
}
