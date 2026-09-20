package com.iptvplayer.app.data

/**
 * Parser de listas M3U pensado para no bloquear la carga con listas grandes:
 *  - [extractCategoryNames] hace un barrido ligero, solo para saber qué
 *    categorías existen (para las pantallas de gestión), sin construir canales.
 *  - [parseChannels] hace el parseo completo, pero solo crea objetos [Channel]
 *    para las categorías/canales que están habilitados, gracias a los predicados.
 */
object M3uParser {

    private val extinfRegex = Regex("""#EXTINF:-?\d+ ?(.*),(.*)""")
    private val attrRegex = Regex("""([a-zA-Z0-9-]+)="([^"]*)"""")

    fun extractCategoryNames(raw: String): List<Pair<ContentType, String>> {
        val seen = LinkedHashSet<Pair<ContentType, String>>()
        raw.lineSequence().forEach { line ->
            if (line.startsWith("#EXTINF")) {
                val match = extinfRegex.find(line) ?: return@forEach
                val attrs = parseAttributes(match.groupValues[1])
                val groupTitle = attrs["group-title"]?.trim().orEmpty().ifBlank { "Sin categoría" }
                val type = classify(groupTitle)
                seen.add(type to groupTitle)
            }
        }
        return seen.toList()
    }

    fun parseChannels(
        raw: String,
        isCategoryEnabled: (ContentType, String) -> Boolean = { _, _ -> true },
        isChannelEnabled: (String) -> Boolean = { true }
    ): List<Channel> {
        val channels = mutableListOf<Channel>()
        var pendingName: String? = null
        var pendingLogo: String? = null
        var pendingGroup: String? = null
        var pendingType: ContentType? = null

        raw.lineSequence().forEach { rawLine ->
            val line = rawLine.trim()
            when {
                line.startsWith("#EXTINF") -> {
                    val match = extinfRegex.find(line)
                    val attrs = match?.let { parseAttributes(it.groupValues[1]) } ?: emptyMap()
                    val displayName = match?.groupValues?.get(2)?.trim().orEmpty()
                    val groupTitle = attrs["group-title"]?.trim().orEmpty().ifBlank { "Sin categoría" }
                    pendingName = attrs["tvg-name"]?.trim()?.ifBlank { null } ?: displayName
                    pendingLogo = attrs["tvg-logo"]?.trim()
                    pendingGroup = groupTitle
                    pendingType = classify(groupTitle)
                }
                line.startsWith("#") || line.isBlank() -> {
                    // comentario u otra directiva M3U: se ignora
                }
                else -> {
                    val type = pendingType
                    val group = pendingGroup
                    if (type != null && group != null && isCategoryEnabled(type, group)) {
                        // El id se deriva de la URL del stream, no del orden de aparición,
                        // para que el estado activado/desactivado sobreviva a recargas de la lista.
                        val id = "ch_${line.hashCode()}"
                        if (isChannelEnabled(id)) {
                            channels.add(
                                Channel(
                                    id = id,
                                    name = pendingName ?: "Canal sin nombre",
                                    logoUrl = pendingLogo,
                                    streamUrl = line,
                                    groupTitle = group,
                                    type = type
                                )
                            )
                        }
                    }
                    pendingName = null
                    pendingLogo = null
                    pendingGroup = null
                    pendingType = null
                }
            }
        }
        return channels
    }

    private fun parseAttributes(segment: String): Map<String, String> =
        attrRegex.findAll(segment).associate { it.groupValues[1] to it.groupValues[2] }

    private fun classify(groupTitle: String): ContentType {
        val g = groupTitle.lowercase()
        return when {
            "serie" in g -> ContentType.SERIES
            "movie" in g || "pelicul" in g || "película" in g || "vod" in g -> ContentType.MOVIE
            else -> ContentType.LIVE
        }
    }
}
