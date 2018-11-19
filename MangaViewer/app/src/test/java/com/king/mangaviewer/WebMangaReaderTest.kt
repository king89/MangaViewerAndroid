package com.king.mangaviewer

import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.data.mangaprovider.WebMangaReader
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WebMangaReaderTest : MangaProviderTestBase() {

    override fun getMangaChapterUrl(): String = "https://www.mangareader.net/naruto"
    override fun getMangaPageUrl(): String = "https://www.mangareader.net/naruto/1"
    override fun getProvider(): MangaProvider = WebMangaReader()

}
