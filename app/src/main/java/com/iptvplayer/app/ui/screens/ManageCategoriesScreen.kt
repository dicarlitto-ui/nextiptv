package com.iptvplayer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iptvplayer.app.data.ContentType
import com.iptvplayer.app.ui.AppViewModel
import com.iptvplayer.app.ui.components.TvFocusable
import com.iptvplayer.app.ui.theme.AccentCyan
import com.iptvplayer.app.ui.theme.BackgroundDark
import com.iptvplayer.app.ui.theme.SurfaceDark
import com.iptvplayer.app.ui.theme.TextMuted
import com.iptvplayer.app.ui.theme.TextPrimary

@Composable
fun ManageCategoriesScreen(
    viewModel: AppViewModel,
    initialType: ContentType,
    onBack: () -> Unit,
    onOpenChannels: (ContentType, String) -> Unit
) {
    val toggles by viewModel.categoryToggles.collectAsStateWithLifecycle()
    var currentType by remember { mutableStateOf(initialType) }
    val filteredToggles = toggles.filter { it.type == currentType }

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
            Text("Gestionar categorías", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContentType.values().forEach { type ->
                TvFocusable(
                    onClick = { currentType = type },
                    backgroundColor = if (type == currentType) AccentCyan.copy(alpha = 0.1f) else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = type.label(),
                        color = if (type == currentType) AccentCyan else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (filteredToggles.isEmpty()) {
            Text("Todavía no hay categorías de este tipo en la lista cargada.", color = TextMuted, fontSize = 12.sp)
        } else {
            LazyColumn {
                items(filteredToggles) { toggle ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(SurfaceDark, RoundedCornerShape(8.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = toggle.enabled,
                            onCheckedChange = { enabled -> viewModel.setCategoryEnabled(toggle.type, toggle.name, enabled) },
                            colors = SwitchDefaults.colors(checkedTrackColor = AccentCyan)
                        )
                        Text(
                            text = toggle.name,
                            color = if (toggle.enabled) TextPrimary else TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f).padding(horizontal = 10.dp)
                        )
                        IconButton(onClick = { onOpenChannels(toggle.type, toggle.name) }) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Ver canales", tint = TextMuted)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
