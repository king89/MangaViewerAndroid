package com.king.mangaviewer.domain.external.mangaprovider

import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.TitleAndUrl
import com.king.mangaviewer.util.GsonHelper
import com.king.mangaviewer.util.LZString
import com.king.mangaviewer.util.Logger
import org.jsoup.Jsoup
import java.util.ArrayList
import java.util.regex.Pattern
import javax.inject.Inject

class WebManhuagui @Inject constructor() : MangaProvider() {

    init {
        WEBSITE_URL = "https://www.manhuagui.com/"
        WEB_SEARCH_URL = "https://www.manhuagui.com/s/%s_p%s.html"
        latestMangaUrl = "https://www.manhuagui.com/update/"
        WEB_ALL_MANGA_BASE_URL = "https://www.manhuagui.com/list/"
        CHARSET = "utf-8"
    }

    override fun getLatestMangaList(
        html: String): List<MangaMenuItem> {
        val topMangaList = ArrayList<TitleAndUrl>()

        val doc = Jsoup.parse(html)
        val el = doc.select(".latest-list .cover")
        for (e in el) {
            val url = checkUrl(e.attr("href")).let {
                if (it.last() != '/') {
                    "$it/"
                } else {
                    it
                }
            }
            val title = e.attr("title")
            val imageUrl = e.select("img").let {
                if (it.attr("src").isEmpty()) {
                    it.attr("data-src")
                } else {
                    it.attr("src")
                }
            }
            topMangaList.add(TitleAndUrl(title, url, imageUrl))
        }

        return toMenuItem(topMangaList)

    }

    override fun getSearchList(html: String): MutableList<TitleAndUrl> {
        val mangaList = ArrayList<TitleAndUrl>()

        val doc = Jsoup.parse(html)
        val el = doc.select(".bcover")
        for (e in el) {
            val url = checkUrl(e.attr("href")).let {
                if (it.last() != '/') {
                    "$it/"
                } else {
                    it
                }
            }
            val title = e.attr("title")
            val imageUrl = e.select("img").let {
                if (it.attr("src").isEmpty()) {
                    it.attr("data-src")
                } else {
                    it.attr("src")
                }
            }
            mangaList.add(TitleAndUrl(title, url, imageUrl))
        }

        return mangaList
    }

    override fun getSearchTotalNum(html: String): Int {
        val doc = Jsoup.parse(html)
        val els = doc.select(".pager a")
        val index = els.last()?.let {
            val pageLink = it.attr("href")
            val p = Pattern.compile("p(\\d+)\\.")
            val m = p.matcher(pageLink)
            if (m.find()) {
                m.group(1).toInt()
            } else {
                0
            }
        } ?: 0
        return index
    }

    override fun getChapterList(menu: MangaMenuItem): MutableList<TitleAndUrl> {
        val chapterList = ArrayList<TitleAndUrl>()

        val html = getHtml(menu.url)
        var doc = Jsoup.parse(html)
        val hidden = doc.select("#__VIEWSTATE").`val`()
        if (hidden.isNotEmpty()) {
            val decodedHtml = LZString.decompressFromBase64(hidden)
            doc = Jsoup.parse(decodedHtml)
        }
        val tabList = doc.select(".chapter-list")
        for (tab in tabList) {
            val liList = tab.select("ul")
            for (el in liList.reversed()) {
                for (e in el.select("li a")) {
                    var url = e.attr("href")
                    val title = e.attr("title")
                    url = checkUrl(url)
                    chapterList.add(TitleAndUrl(title, url))
                }
            }
        }


        return chapterList
    }

    override fun getPageList(firstPageUrl: String): MutableList<String> {

        val html = getHtml(firstPageUrl)
        return decodePageList(html).toMutableList()
    }

    private fun decodePageList(html: String): List<String> {

        try {

            var pattern = Pattern.compile("(?<=\\}\\(').+?</script>")
            var m = pattern.matcher(html)
            return if (m.find()) {
                var result = m.group()
                Logger.d(TAG, result)
                pattern = Pattern.compile("\\,(?=([^\']*\'[^\']*\')*[^\']*\$)")
                val array = pattern.split(result).toList()

                val codedJson = array[0].let {
                    pattern = Pattern.compile("\\{.+\\}")
                    m = pattern.matcher(it)
                    if (m.find()) {
                        return@let m.group()
                    } else {
                        null
                    }
                }!!

                val num1 = array[1].toInt()
                val num2 = array[2].toInt()
                val d = array[3].let {
                    pattern = Pattern.compile("'(.+?)'")
                    m = pattern.matcher(it)
                    if (m.find()) {
                        return@let m.group(1)
                    } else {
                        null
                    }
                }

                val decodedStringList = LZString.decompressFromBase64(d).split("|")
                Logger.d(TAG, "$decodedStringList")

                val pattern = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                val stringBuilder = StringBuilder()
                var charToParse = StringBuilder()
                var index = 0
                while (index < codedJson.length) {
                    val s = codedJson[index]
                    if (s !in pattern) {
                        if (charToParse.isNotEmpty()) {
                            stringBuilder.append(
                                decodedStringList[getInt(charToParse.toString(), num1,
                                    num2)].let {
                                    if (it.isEmpty()) {
                                        charToParse.toString()
                                    } else {
                                        it
                                    }
                                })
                        }

                        stringBuilder.append(s)
                        charToParse = StringBuilder()
                    } else {
                        charToParse.append(s)
                    }
                    index++

                }
//                Logger.d(TAG, "$stringBuilder")

                val imageInfo = GsonHelper.fromJson<ImageInfo>(stringBuilder.toString(),
                    ImageInfo::class.java)
                val resultList = mutableListOf<String>()
                for (i in imageInfo.files!!) {
                    resultList.add(i.let {
                        val imageServer = "https://i.hamreus.com"
                        "$imageServer${imageInfo.path}$i?cid=${imageInfo.cid}&md5=${imageInfo.sl?.md5}"
                    })
                }
                resultList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Logger.e(TAG, e, "error")
            return emptyList()
        }
    }

    private fun getInt(s: String, num1: Int, num2: Int): Int {
        var result = 0
        try {
            for (c in s.toCharArray()) {
                result = Integer.parseInt(c.toString(), 36) + result * num1
                if (Character.isUpperCase(c)) {
                    result += 26
                }
            }
            return result
        } catch (e: Exception) {
        }

        return result
    }

    companion object {
        val TAG = WebManhuagui::class.java.simpleName
    }
}

data class ImageInfo(
    val bid: Int? = null,
    val bname: String? = null,
    val bpic: String? = null,
    val cid: Int? = null,
    val cname: String? = null,
    val files: List<String>? = null,
    val finished: Boolean? = null,
    val len: Int? = null,
    val path: String? = null,
    val status: Int? = null,
    val blockCc: String? = null,
    val nextId: Int? = null,
    val prevId: Int? = null,
    val sl: Sl? = null
)

data class Sl(
    val md5: String? = null
)