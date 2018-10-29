package com.king.mangaviewer

import android.content.Context
import com.king.mangaviewer.domain.data.mangaprovider.WebDMZJ
import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.HashMap

@RunWith(MockitoJUnitRunner::class)
class WebDMZJTest {

    @Mock
    lateinit var mMockContext: Context

    lateinit var webSite: MangaProvider

    @Before
    fun setup() {
        webSite = WebDMZJ(mMockContext)

    }

    @Test
    fun getMangaList() {

        val hashMap = HashMap<String, Any>()
        val list = webSite.getLatestMangaList(hashMap)
        println(list.first())
        Assert.assertTrue(list.size > 0)
    }

    @Test
    fun getChapterList() {
        val url = "https://manhua.dmzj.com/wojianvpushibiantai/"
        val list = webSite.getChapterList(url)
        println(list.first())
        Assert.assertTrue(list.size > 0)
    }

    @Test
    fun getPageList() {
        val url = "https://manhua.dmzj.com/wojianvpushibiantai/70128.shtml#@page=1"
        val list = webSite.getPageList(url)
        println(list.first())
        Assert.assertTrue(list.size > 0)
    }

    @Test
    fun searchManga() {

    }

}
