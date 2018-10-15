package com.king.mangaviewer.model

import com.google.gson.annotations.SerializedName

data class MangaUri(
        @SerializedName("imageUri")
        val imageUri: String,
        @SerializedName("referUri")
        val referUri: String)