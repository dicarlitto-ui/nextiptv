package com.iptvplayer.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class M3uParserTest {

    private val sample = """
        #EXTM3U
        #EXTINF:-1 tvg-name="Canal Uno" tvg-logo="http://x/1.png" group-title="Entretenimiento",Canal Uno
        http://ejemplo.com/uno.m3u8
        #EXTINF:-1 tvg-name="Deportes Plus" group-title="Deportes",Deportes Plus
        http://ejemplo.com/deportes.m3u8
        #EXTINF:-1 tvg-name="Peli Uno" group-title="Cine VOD",Peli Uno
        http://ejemplo.com/peli.mp4
        #EXTINF:-1 tvg-name="Serie Uno" group-title="Series Drama",Serie Uno
        http://ejemplo.com/serie.m3u8
    """.trimIndent()

    @Test
    fun `extractCategoryNames encuentra todas las categorias sin construir canales`() {
        val categories = M3uParser.extractCategoryNames(sample)
        assertEquals(4, categories.size)
        assertTrue(categories.contains(ContentType.LIVE to "Entretenimiento"))
        assertTrue(categories.contains(ContentType.MOVIE to "Cine VOD"))
        assertTrue(categories.contains(ContentType.SERIES to "Series Drama"))
    }

    @Test
    fun `parseChannels clasifica el tipo de contenido segun el group-title`() {
        val channels = M3uParser.parseChannels(sample)
        assertEquals(4, channels.size)
        assertEquals(ContentType.LIVE, channels.first { it.name == "Canal Uno" }.type)
        assertEquals(ContentType.MOVIE, channels.first { it.name == "Peli Uno" }.type)
        assertEquals(ContentType.SERIES, channels.first { it.name == "Serie Uno" }.type)
    }

    @Test
    fun `parseChannels omite categorias desactivadas para acelerar la carga`() {
        val channels = M3uParser.parseChannels(
            raw = sample,
            isCategoryEnabled = { _, group -> group != "Deportes" }
        )
        assertEquals(3, channels.size)
        assertTrue(channels.none { it.groupTitle == "Deportes" })
    }

    @Test
    fun `el id de un canal es estable entre dos parseos de la misma lista`() {
        val first = M3uParser.parseChannels(sample)
        val second = M3uParser.parseChannels(sample)
        assertEquals(first.map { it.id }, second.map { it.id })
    }
}
