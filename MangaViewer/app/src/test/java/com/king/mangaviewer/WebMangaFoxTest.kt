package com.king.mangaviewer

import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.external.mangaprovider.WebMangaFox
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WebMangaFoxTest : MangaProviderTestBase() {

    override fun getMangaChapterUrl(): String = "https://fanfox.net/manga/dragon_and_the_phoenix/"
    override fun getMangaPageUrl(): String = "https://fanfox.net/manga/dragon_and_the_phoenix/c001/1.html"
    override fun getProvider(): MangaProvider = WebMangaFox()


    @Test
    fun testGetTotalNum() {
        val s = """ <script type="text/javascript">
            var csshost = "https://static.fanfox.net/v2018118/mangafox/";
            var comicid = 29578;
            var chapterid = 575140;
            var userid = 0;
            var imagepage = 1;
            var imagecount = 55;
            var pagerrefresh = false;
            var pagetype = 2;
            var postpageindex = 1;
            var postpagecount = 0;
            var postcount = 0;
            var postsort = 0;
            var topicId = 0;
            var prechapterurl = "";
            var nextchapterurl = "/manga/gokutei_higuma/c002/1.html";
        </script>"""

        val num = getProvider().getTotalNum(s)
        assertEquals(55, num)
    }
}
