package com.king.mangaviewer.util

import com.king.mangaviewer.model.MangaWebSource

fun getMangaWebSource(): MangaWebSource {
    val json = """{"id": "999",
    "name": "TestManga",
    "displayName": "TestManga",
    "className": "com.king.mangaviewer.domain.external.mangaprovider.WebTestManga",
    "order": "999",
    "language": "",
    "enable": "1"}"""
    return GsonHelper.fromJson(json, MangaWebSource::class.java)
}