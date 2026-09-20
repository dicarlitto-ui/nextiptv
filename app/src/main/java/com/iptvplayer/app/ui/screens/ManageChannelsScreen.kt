package com.iptvplayer.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.iptvplayer.app.data.ContentType
import com.iptvplayer.app.ui.AppViewModel
import com.iptvplayer.app.ui.components.ChannelLogo
import com.iptvplayer.app.ui.theme.AccentCyan
import com.iptvplayer.app.ui.theme.BackgroundDark
import com.iptvplayer.app.ui.theme.SurfaceDark
import com.iptvplayer.app.ui.theme.TextMuted
import com.iptvplayer.app.ui.theme.TextPrimary

@Composable
fun ManageChannelsScreen(
    viewModel: AppViewModel,
    type: ContentType,
    categoryName: String,
    onBack: () -> Unit
) {
    val channels = remember(type, categoryName) { viewModel.channelsInCategory(type, categoryName) }
    val disabledIds by viewModel.disabledChannelIds.collectAsStateWithLifecycle()

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
            Text(categoryName, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
        Text(
            text = "${channels.size} canales",
            color = TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 48.dp)
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(channels) { channel ->
                val enabled = channel.id !in disabledIds
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark, RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        ChannelLogo(name = channel.name, logoUrl = channel.logoUrl, size = 24.dp)
                        Text(
                            text = channel.name,
                            color = if (enabled) TextPrimary else TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = { newValue -> viewModel.setChannelEnabled(channel.id, newValue) },
                        colors = SwitchDefaults.colors(checkedTrackColor = AccentCyan)
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
