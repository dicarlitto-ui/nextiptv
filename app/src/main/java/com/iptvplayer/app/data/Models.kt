package com.iptvplayer.app.data

enum class ContentType {
    LIVE, MOVIE, SERIES
}

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String?,
    val streamUrl: String,
    val groupTitle: String,
    val type: ContentType
)

data class Category(
    val name: String,
    val type: ContentType,
    val channels: List<Channel>
)
