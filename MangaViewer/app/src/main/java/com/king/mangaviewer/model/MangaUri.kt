package com.king.mangaviewer.model

import com.king.mangaviewer.model.MangaUriType.WEB

data class MangaUri(
    val imageUri: String,
    val referUri: String,
    val type: MangaUriType = WEB
)

enum class MangaUriType {
    WEB,
    ZIP
}