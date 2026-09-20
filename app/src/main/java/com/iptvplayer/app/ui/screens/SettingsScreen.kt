package com.iptvplayer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iptvplayer.app.data.ContentType
import com.iptvplayer.app.ui.components.TvFocusable
import com.iptvplayer.app.ui.theme.BackgroundDark
import com.iptvplayer.app.ui.theme.TextMuted
import com.iptvplayer.app.ui.theme.TextPrimary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onChangePlaylist: () -> Unit,
    onManageCategories: (ContentType) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = TextMuted)
            }
            Text("Configuración", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(20.dp))

        SettingsRow("Cambiar lista de reproducción", onChangePlaylist)
        Spacer(Modifier.height(8.dp))
        SettingsRow("Gestionar categorías · En vivo") { onManageCategories(ContentType.LIVE) }
        Spacer(Modifier.height(8.dp))
        SettingsRow("Gestionar categorías · Películas") { onManageCategories(ContentType.MOVIE) }
        Spacer(Modifier.height(8.dp))
        SettingsRow("Gestionar categorías · Series") { onManageCategories(ContentType.SERIES) }
    }
}

@Composable
private fun SettingsRow(label: String, onClick: () -> Unit) {
    TvFocusable(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = TextMuted)
        }
    }
}
