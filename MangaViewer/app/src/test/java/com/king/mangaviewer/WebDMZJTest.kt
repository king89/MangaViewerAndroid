package com.king.mangaviewer

import android.content.Context
import com.king.mangaviewer.domain.external.mangaprovider.WebDMZJ
import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WebDMZJTest : MangaProviderTestBase() {

    @Mock
    lateinit var mMockContext: Context
    lateinit var webSite: MangaProvider

    override fun getMangaChapterUrl(): String = "https://manhua.dmzj.com/wojianvpushibiantai/"
    override fun getMangaPageUrl(): String = "https://manhua.dmzj.com/wojianvpushibiantai/70128.shtml#@page=1"
    override fun getProvider(): MangaProvider = WebDMZJ()

}
