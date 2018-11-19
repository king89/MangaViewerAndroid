package com.king.mangaviewer

import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.data.mangaprovider.WebMangaFox
import com.king.mangaviewer.domain.data.mangaprovider.WebMangaReader
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WebMangaFoxTest : MangaProviderTestBase() {

    override fun getMangaChapterUrl(): String = "https://fanfox.net/manga/dragon_and_the_phoenix/"
    override fun getMangaPageUrl(): String = "https://fanfox.net/manga/dragon_and_the_phoenix/c001/1.html"
    override fun getProvider(): MangaProvider = WebMangaFox()

}
