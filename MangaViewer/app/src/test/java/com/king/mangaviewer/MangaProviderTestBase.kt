package com.king.mangaviewer

import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider.Companion.STATE_SEARCH_QUERYTEXT
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
    fun setup() {
        wbp = getProvider()
        wbp.okHttpClient = OkHttpClient.Builder().build()
    }

    abstract fun getProvider(): MangaProvider
    abstract fun getMangaChapterUrl(): String
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

        val url = getMangaChapterUrl()
        val list = wbp.getChapterList(url)!!
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
}

