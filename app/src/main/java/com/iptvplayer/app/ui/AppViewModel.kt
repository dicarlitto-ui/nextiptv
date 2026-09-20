package com.iptvplayer.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iptvplayer.app.data.Category
import com.iptvplayer.app.data.Channel
import com.iptvplayer.app.data.ContentType
import com.iptvplayer.app.data.M3uParser
import com.iptvplayer.app.data.SettingsDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

data class CategoryToggle(
    val type: ContentType,
    val name: String,
    val enabled: Boolean
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsDataStore(application)

    private val rawPlaylist = MutableStateFlow<String?>(null)
    private val allCategoryNames = MutableStateFlow<List<Pair<ContentType, String>>>(emptyList())

    val isLoading = MutableStateFlow(false)
    val loadError = MutableStateFlow<String?>(null)

    val visibleCategories: StateFlow<List<Category>> = combine(
        rawPlaylist, settings.disabledCategoryKeys, settings.disabledChannelIds
    ) { raw, disabledCats, disabledChannels ->
        if (raw.isNullOrBlank()) {
            emptyList()
        } else {
            M3uParser.parseChannels(
                raw = raw,
                isCategoryEnabled = { type, name ->
                    SettingsDataStore.categoryKey(type, name) !in disabledCats
                },
                isChannelEnabled = { id -> id !in disabledChannels }
            ).groupBy { it.type to it.groupTitle }
                .map { (key, channels) -> Category(name = key.second, type = key.first, channels = channels) }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val categoryToggles: StateFlow<List<CategoryToggle>> = combine(
        allCategoryNames, settings.disabledCategoryKeys
    ) { names, disabled ->
        names.map { (type, name) ->
            CategoryToggle(type, name, SettingsDataStore.categoryKey(type, name) !in disabled)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val disabledChannelIds: StateFlow<Set<String>> =
        settings.disabledChannelIds.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    init {
        viewModelScope.launch {
            val savedUrl = settings.playlistUrl
            savedUrl.collect { url ->
                if (!url.isNullOrBlank() && rawPlaylist.value == null) {
                    loadPlaylist(url)
                }
            }
        }
    }

    fun loadPlaylist(url: String) {
        viewModelScope.launch {
            isLoading.value = true
            loadError.value = null
            try {
                val text = withContext(Dispatchers.IO) {
                    URL(url).openStream().bufferedReader().use { it.readText() }
                }
                rawPlaylist.value = text
                allCategoryNames.value = M3uParser.extractCategoryNames(text)
                settings.setPlaylistUrl(url)
            } catch (e: Exception) {
                loadError.value = e.message ?: "No se pudo cargar la lista"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun setCategoryEnabled(type: ContentType, name: String, enabled: Boolean) {
        viewModelScope.launch {
            settings.setCategoryEnabled(SettingsDataStore.categoryKey(type, name), enabled)
        }
    }

    fun setChannelEnabled(channelId: String, enabled: Boolean) {
        viewModelScope.launch { settings.setChannelEnabled(channelId, enabled) }
    }

    /** Todos los canales de una categoría (activos e inactivos), para la pantalla de gestión. */
    fun channelsInCategory(type: ContentType, name: String): List<Channel> {
        val raw = rawPlaylist.value ?: return emptyList()
        return M3uParser.parseChannels(
            raw = raw,
            isCategoryEnabled = { t, g -> t == type && g == name },
            isChannelEnabled = { true }
        )
    }
}
