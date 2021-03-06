package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.model.MangaMenuItem
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WebMangaReaderTest : MangaProviderTestBase() {

    override fun getMangaMenu(): MangaMenuItem = generateMenu().apply { url = "https://www.mangareader.net/naruto" }
    override fun getMangaPageUrl(): String = "https://www.mangareader.net/naruto/1"
    override fun getProvider(): MangaProvider = WebMangaReader()

}
