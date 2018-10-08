package com.king.mangaviewer

import android.content.Context
import com.king.mangaviewer.MangaPattern.WebManhuagui
import com.king.mangaviewer.MangaPattern.WebSiteBasePattern
import com.king.mangaviewer.MangaPattern.WebSiteBasePattern.STATE_SEARCH_QUERYTEXT
import com.king.mangaviewer.util.LZString
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.HashMap

@RunWith(MockitoJUnitRunner::class)
class WebManhuaguiTest {

    @Mock
    lateinit var mMockContext: Context
    lateinit var wbp: WebSiteBasePattern

    @Before
    fun setup() {
        wbp = WebManhuagui(mMockContext)
    }

    @Test
    fun getMangaList() {

        val hashMap = HashMap<String, Any>()
        val list = wbp.getLatestMangaList(hashMap)
        println(list.first())
        assertTrue(list.size > 0)
    }

    @Test
    fun getChapterList() {

        val url = "https://www.manhuagui.com/comic/18467/"
        val list = wbp.getChapterList(url)
        println(list.first())
        assertTrue(list.size > 0)
    }

    @Test
    fun getHiddenChapterList() {

        val url = "https://www.manhuagui.com/comic/19534/"
        val list = wbp.getChapterList(url)
        println(list.first())
        assertTrue(list.size > 0)
    }

    @Test
    fun getPageList() {

        val url = "https://www.manhuagui.com/comic/18467/395378.html"
        val list = wbp.getPageList(url)
        println(list.first())
        assertTrue(list.size > 0)

    }

    @Test
    fun searchMangaNoPage() {
        val hashState = HashMap<String, Any>().apply {
            this[STATE_SEARCH_QUERYTEXT] = "one piece"
        }
        val list = wbp.getSearchingList(hashState)
        println(list.first())
        assertTrue(list.size > 0)
    }

    @Test
    fun searchMangaMorePage() {
        val hashState = HashMap<String, Any>().apply {
            this[STATE_SEARCH_QUERYTEXT] = "a"
        }
        val list = wbp.getSearchingList(hashState)
        println(list.first())
        println(hashState)
        assertTrue(list.size > 0)
    }

    @Test
    fun testLZString() {
        val s = """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"""

        println(LZString.decompressFromBase64(s))

    }
}

