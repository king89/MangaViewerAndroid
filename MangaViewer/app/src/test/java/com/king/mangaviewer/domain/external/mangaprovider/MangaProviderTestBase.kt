package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider.Companion.STATE_SEARCH_QUERYTEXT
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.model.MangaWebSource.Companion
import junit.framework.Assert.assertTrue
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.runners.MockitoJUnitRunner
import java.util.HashMap

@Ignore
@RunWith(MockitoJUnitRunner::class)
abstract class MangaProviderTestBase {

    @Mock
    lateinit var wbp: MangaProvider

    @Before
    open fun setup() {
        wbp = getProvider()
        wbp.okHttpClient = OkHttpClient.Builder().build()
        wbp.mangaWebSource = MangaWebSource.LOCAL
    }

    abstract fun getProvider(): MangaProvider
    abstract fun getMangaMenu(): MangaMenuItem
    abstract fun getMangaPageUrl(): String

    @Test
    fun getMangaList() {
        val hashMap = HashMap<String, Any>()
        val list = wbp.getLatestMangaList(hashMap)!!
        println(list.first())
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun getChapterList() {

        val menu = getMangaMenu()
        val list = wbp.getChapterList(menu)!!
        println(list.first())
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun getPageList() {
        val url = getMangaPageUrl()
        val list = wbp.getPageList(url)
        println(list.first())
        println(list)
        assertTrue(list.isNotEmpty())

        val imageUrl = wbp.getImageUrl(list.first(), 0)
        println(imageUrl)
        assertTrue(imageUrl.isNotEmpty())
    }

    @Test
    fun searchMangaNoPage() {
        val hashState = HashMap<String, Any>().apply {
            this[STATE_SEARCH_QUERYTEXT] = "one piece"
        }
        val list = wbp.getSearchingList(hashState)!!
        println(list.first())
        assertTrue(list.isNotEmpty())
    }

    @Test
    fun searchMangaMorePage() {
        val hashState = HashMap<String, Any>().apply {
            this[STATE_SEARCH_QUERYTEXT] = "one"
        }
        val list = wbp.getSearchingList(hashState)!!
        println(list.first())
        println(hashState)
        assertTrue(list.isNotEmpty())
    }

    fun generateMenu(): MangaMenuItem {
        return MangaMenuItem("", "", "", "", "", MangaWebSource.LOCAL)
    }
}


