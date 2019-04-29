package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.TitleAndUrl

import java.util.Collections
import java.util.regex.Pattern
import org.jsoup.Jsoup

import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by KinG on 8/31/2015.
 */
class WebMangaFox @Inject constructor(): MangaProvider() {
    init {
        WEBSITE_URL = "https://fanfox.net/"
        latestMangaUrl = "https://fanfox.net/releases/"
        WEB_SEARCH_URL = "https://fanfox.net/search.php?name_method=cw&name=%s&page=%d%s"
        WEB_ALL_MANGA_BASE_URL = "https://fanfox.net/directory/%d.htm"
        CHARSET = "utf8"
    }

    //Menu
    override fun getLatestMangaList(
        html: String): List<MangaMenuItem> {
        val topMangaList = ArrayList<TitleAndUrl>()

        val doc = Jsoup.parse(html)
        val el = doc.select(".manga-list-4-list > li > a")
        for (e in el) {
            val url = checkUrl(e.attr("href"))
            val title = e.attr("title")
            val imageUrl = e.select("img").attr("src")
            topMangaList.add(TitleAndUrl(title, url, imageUrl))
        }

        return toMenuItem(topMangaList)

    }


    override fun getAllMangaList(html: String): List<TitleAndUrl>? {
        val mangaList = ArrayList<TitleAndUrl>()

        val doc = Jsoup.parse(html)
        val el = doc.select(".list li")
        for (i in el.indices) {
            val title = el[i].select(".title").text()
            val url = checkUrl(el[i].select(".title").attr("href"))
            val imageUrl = el[i].select(".manga_img img").attr("src")
            mangaList.add(TitleAndUrl(title, url, imageUrl))
        }
        return mangaList
    }

    override fun getSearchList(html: String): List<TitleAndUrl>? {
        val mangaList = ArrayList<TitleAndUrl>()

        val doc = Jsoup.parse(html)
        val el = doc.select(".series_preview")
        for (i in el.indices) {
            val title = el[i].text()
            val url = checkUrl(el[i].attr("href"))
            mangaList.add(TitleAndUrl(title, url))
        }
        return mangaList
    }

    override fun getSearchTotalNum(html: String): Int {
        val doc = Jsoup.parse(html)
        val els = doc.select("#nav ul li")
        val index = els.size - 2
        return Integer.parseInt(els[index].text())
    }

    override fun getAllMangaTotalNum(html: String): Int {
        return getSearchTotalNum(html)
    }

    override fun getSearchUrl(queryText: String, pageNum: Int): String {
        return String.format(WEB_SEARCH_URL, queryText, pageNum, mRestSearchString)
    }

    //Chapter

    override fun getChapterList(chapterUrl: String): List<TitleAndUrl>? {
        val html = getHtml(chapterUrl)
        val list = ArrayList<TitleAndUrl>()

        val doc = Jsoup.parse(html)
        val els = doc.select(".detail-main-list li a")
        for (e in els) {
            val url = checkUrl(e.attr("href"))
            val title = e.attr("title")
            list.add(TitleAndUrl(title, url))
        }
        Collections.reverse(list)
        return list

    }


    //Page
    override fun getPageList(firstPageUrl: String): List<String> {
        val pageList = ArrayList<String>()
        val html = getHtml(firstPageUrl)
        val total = getTotalNum(html)

        val fileName = firstPageUrl.substring(firstPageUrl.lastIndexOf("/"))
        val preFileName = firstPageUrl.substring(0, firstPageUrl.lastIndexOf("/"))

        //https://fanfox.net/manga/gokutei_higuma/c001/chapterfun.ashx?cid=575140&page=3&key=f3fde960187c0cb7
        //use this handler to get image list

        if (fileName.isEmpty()) {
            for (i in 1..total) {
                pageList.add("$preFileName$i.html")
            }
        } else {
            for (i in 1..total) {
                pageList.add(preFileName + fileName.replace("1", i.toString() + ""))
            }
        }
        return pageList
    }

    override fun getImageUrl(pageUrl: String, nowPage: Int): String {
        val html = getHtml(pageUrl)
        val doc = Jsoup.parse(html)
        return doc.select("#image").attr("src")
    }

    override fun getTotalNum(html: String): Int {

        val codeRe = Pattern.compile("(imagecount.+?)([0-9]+)")
        val m = codeRe.matcher(html)
        return if (m.find()) {
            Integer.parseInt(m.group(2))
        } else 0

    }

    companion object {


        private val mRestSearchString = "&type=&author_method=cw&author=&artist_method=cw&artist=&genres%5BAction%5D=0&genres%5BAdult%5D=0&genres%5BAdventure%5D=0&genres%5BComedy%5D=0&genres%5BDoujinshi%5D=0&genres%5BDrama%5D=0&genres%5BEcchi%5D=0&genres%5BFantasy%5D=0&genres%5BGender+Bender%5D=0&genres%5BHarem%5D=0&genres%5BHistorical%5D=0&genres%5BHorror%5D=0&genres%5BJosei%5D=0&genres%5BMartial+Arts%5D=0&genres%5BMature%5D=0&genres%5BMecha%5D=0&genres%5BMystery%5D=0&genres%5BOne+Shot%5D=0&genres%5BPsychological%5D=0&genres%5BRomance%5D=0&genres%5BSchool+Life%5D=0&genres%5BSci-fi%5D=0&genres%5BSeinen%5D=0&genres%5BShoujo%5D=0&genres%5BShoujo+Ai%5D=0&genres%5BShounen%5D=0&genres%5BShounen+Ai%5D=0&genres%5BSlice+of+Life%5D=0&genres%5BSmut%5D=0&genres%5BSports%5D=0&genres%5BSupernatural%5D=0&genres%5BTragedy%5D=0&genres%5BWebtoons%5D=0&genres%5BYaoi%5D=0&genres%5BYuri%5D=0&released_method=eq&released=&rating_method=eq&rating=&is_completed=&advopts=1&sort=views&order=za"
        private val LOG_TAG = "WebMangaFox"
    }
}
