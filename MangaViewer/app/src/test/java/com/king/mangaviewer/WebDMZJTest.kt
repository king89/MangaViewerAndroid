package com.king.mangaviewer

import android.content.Context
import com.king.mangaviewer.MangaPattern.WebDMZJ
import com.king.mangaviewer.MangaPattern.WebManhuagui
import com.king.mangaviewer.MangaPattern.WebSiteBasePattern
import com.king.mangaviewer.util.LZString
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

    lateinit var webSite: WebSiteBasePattern

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
        val url = "https://www.manhuagui.com/comic/18467/"
        val list = webSite.getChapterList(url)
        println(list.first())
        Assert.assertTrue(list.size > 0)
    }

    @Test
    fun getHiddenChapterList() {
        val url = "https://www.manhuagui.com/comic/19534/"
        val list = webSite.getChapterList(url)
        println(list.first())
        Assert.assertTrue(list.size > 0)
    }

    @Test
    fun getPageList() {
        val url = "https://www.manhuagui.com/comic/18467/395378.html"
        val list = webSite.getPageList(url)
        println(list.first())
        Assert.assertTrue(list.size > 0)
    }

    @Test
    fun searchManga() {

    }

}
