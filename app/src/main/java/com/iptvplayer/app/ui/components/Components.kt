package com.iptvplayer.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iptvplayer.app.ui.theme.AccentCyan
import com.iptvplayer.app.ui.theme.SurfaceDark

/**
 * Contenedor con foco visible pensado para mando a distancia: al recibir foco
 * (D-pad) resalta el borde en el color de acento, además de responder al click.
 */
@Composable
fun TvFocusable(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    backgroundColor: Color = SurfaceDark,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = if (isFocused) AccentCyan else Color.Transparent,
                shape = shape
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
    ) {
        content()
    }
}

private val logoPalette = listOf(
    Color(0xFF2E86DE), Color(0xFF16A085), Color(0xFF8E44AD),
    Color(0xFFD35400), Color(0xFFC2185B), Color(0xFFC23B3B), Color(0xFFF1C40F)
)

fun colorForName(name: String): Color {
    val idx = (name.hashCode() and 0x7fffffff) % logoPalette.size
    return logoPalette[idx]
}

@Composable
fun ChannelLogo(name: String, logoUrl: String?, size: Dp = 32.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(4.dp))
            .background(colorForName(name)),
        contentAlignment = Alignment.Center
    ) {
        if (!logoUrl.isNullOrBlank()) {
            AsyncImage(
                model = logoUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } else {
            Text(
                text = name.trim().take(2).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
