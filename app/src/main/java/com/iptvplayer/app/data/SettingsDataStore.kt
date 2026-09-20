package com.iptvplayer.app.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "iptv_settings")

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val PLAYLIST_URL = stringPreferencesKey("playlist_url")
        val DISABLED_CATEGORIES = stringSetPreferencesKey("disabled_categories")
        val DISABLED_CHANNELS = stringSetPreferencesKey("disabled_channels")
    }

    val playlistUrl: Flow<String?> =
        context.dataStore.data.map { it[Keys.PLAYLIST_URL] }

    val disabledCategoryKeys: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.DISABLED_CATEGORIES] ?: emptySet() }

    val disabledChannelIds: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.DISABLED_CHANNELS] ?: emptySet() }

    suspend fun setPlaylistUrl(url: String) {
        context.dataStore.edit { it[Keys.PLAYLIST_URL] = url }
    }

    suspend fun setCategoryEnabled(categoryKey: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.DISABLED_CATEGORIES] ?: emptySet()
            prefs[Keys.DISABLED_CATEGORIES] = if (enabled) current - categoryKey else current + categoryKey
        }
    }

    suspend fun setChannelEnabled(channelId: String, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.DISABLED_CHANNELS] ?: emptySet()
            prefs[Keys.DISABLED_CHANNELS] = if (enabled) current - channelId else current + channelId
        }
    }

    companion object {
        fun categoryKey(type: ContentType, name: String) = "${type.name}::$name"
    }
}
