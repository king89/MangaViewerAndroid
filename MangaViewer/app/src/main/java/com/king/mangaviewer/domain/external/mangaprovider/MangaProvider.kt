package com.king.mangaviewer.domain.external.mangaprovider

import android.os.Environment
import android.util.Log
import com.king.mangaviewer.MyApplication
import com.king.mangaviewer.common.Constants
import com.king.mangaviewer.common.Constants.SaveType
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaPageItem
import com.king.mangaviewer.model.MangaUriType.WEB
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.model.TitleAndUrl
import com.king.mangaviewer.util.FileHelper
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.NetworkHelper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.util.ArrayList
import java.util.HashMap
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class MangaProvider {
    var WEBSITE_URL = ""
    var WEB_SEARCH_URL = ""
    var WEB_ALL_MANGA_BASE_URL = ""
    var latestMangaUrl = ""
    var CHARSET = "utf8"

    protected var startNum = 1
    protected var totalNum = 1
    protected var firstPageHtml: String? = null

    lateinit var okHttpClient: OkHttpClient
    lateinit var mangaWebSource: MangaWebSource

    // Check if have external storage
    val mangaFolder: String
        get() {
            val context = MyApplication.context
            return if (Environment.getExternalStorageState() === Environment.MEDIA_MOUNTED) {
                (context.getExternalFilesDir(null).toString() + File.separator
                    + Constants.MANGAFOLDER + File.separator
                    + this.javaClass.simpleName)
            } else {
                (context.filesDir.toString() + File.separator
                    + Constants.MANGAFOLDER + File.separator
                    + this.javaClass.simpleName)
            }
        }

    open fun getPageList(firstPageUrl: String): List<String> {
        val list = ArrayList<String>()
        val prefix = "http://www.imanhua.com/comic/1067/list_104097.html?p="
        for (i in 0..9) {
            list.add(prefix + i)
        }
        return list
    }

    open fun getTotalNum(html: String): Int {
        val r = Pattern.compile("value=\"[0-9]+\"")
        val m = r.matcher(html)

        val r2 = Pattern.compile("[0-9]+")

        var m2: Matcher? = null
        var max = -9
        while (m.find()) {
            val tmp = m.group()
            m2 = r2.matcher(tmp)
            m2!!.find()
            val now = Integer.parseInt(m2.group())
            if (max < now) {
                max = now
            }

        }

        return if (max > 0) {
            max
        } else {
            0
        }

    }

    fun getImageUrl(pageUrl: String): String? {
        return null
    }

    open fun getImageUrl(pageUrl: String, nowPage: Int): String {
        return pageUrl
    }

    @JvmOverloads
    protected fun checkUrl(url: String, isHttps: Boolean = false): String {
        var url = url
        var httpType = "http"
        if (isHttps) {
            httpType = "https"
        }
        if (url.startsWith("//")) {
            url = "$httpType:$url"
        } else if (url.startsWith("/")) {
            url = WEBSITE_URL + url.substring(1)
        } else if (!url.startsWith(httpType)) {
            url = WEBSITE_URL + url
        }
        //remove last "/"
        if (url.endsWith("/") && url.length > 1) {
            url = url.substring(0, url.length - 1)
        }
        return url
    }

    // public void DownloadOnePage(String pageUrl,String folder,int nowPageNum)
// { return; }
    fun getHtml(urlString: String): String {
        val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36"
        val request = Request.Builder()
            .addHeader("User-Agent", userAgent)
            .url(urlString)
            .get()
            .build()
        val response = okHttpClient.newCall(request).execute()
        return response.body()!!.string()
    }

    fun getPrePageImageFilePath(imgUrl: String, pageItem: MangaPageItem): String {
        val folderName = (mangaFolder + File.separator
            + pageItem.folderPath)
        val fileName = FileHelper.getFileName(imgUrl)
        val dir = File(folderName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir.absolutePath + File.separator
            + fileName)
        return file.absolutePath
    }

    open fun DownloadImgPage(imgUrl: String, pageItem: MangaPageItem,
        saveType: SaveType, refer: String?): String? {
        var refer = refer
        if (refer == null || refer === "") {
            refer = this.WEBSITE_URL
        }
        val folderName = (mangaFolder + File.separator
            + pageItem.folderPath)
        val fileName = FileHelper.getFileName(imgUrl)
        try {
            val inputStream = NetworkHelper.downLoadFromUrl(imgUrl, refer)
            return FileHelper.saveFile(folderName, fileName, inputStream)
        } catch (e1: MalformedURLException) {
            e1.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    /*
     * // // Chapter //
     */
    open fun getChapterList(chapterUrl: String): List<TitleAndUrl>? {
        return null
    }

    /*
     * // // Menu //
     */
    open fun getLatestMangaList(
        state: HashMap<String, Any>): List<MangaMenuItem> {
        val html = getHtml(latestMangaUrl)
        if (html.isEmpty()) {
            return emptyList()
        }
        state[STATE_NO_MORE] = true
        return getLatestMangaList(html)
    }

    protected open fun getLatestMangaList(
        html: String): List<MangaMenuItem> {
        return emptyList()
    }

    fun GetNewMangaList(html: String): List<TitleAndUrl>? {
        return null
    }

    /*Searching Manga*/
    open fun getSearchingList(state: HashMap<String, Any>): List<TitleAndUrl>? {
        var noMore = false
        if (state.containsKey(STATE_NO_MORE)) {
            noMore = state[STATE_NO_MORE] as Boolean
        }

        if (!noMore) {
            var queryText = state[STATE_SEARCH_QUERYTEXT].toString()
            try {
                queryText = java.net.URLEncoder.encode(queryText, CHARSET)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            var pageNum = 1
            var totalNum = if (state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY))
                state[STATE_TOTAL_PAGE_NUM_THIS_KEY] as Int
            else
                0
            var html = ""
            //no total num means first time
            if (state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY)) {
                if (state.containsKey(STATE_PAGE_NUM_NOW)) {
                    pageNum = state[STATE_PAGE_NUM_NOW] as Int
                }
                if (pageNum + 1 <= totalNum) {
                    pageNum++
                    state[STATE_PAGE_NUM_NOW] = pageNum
                } else {
                    state[STATE_NO_MORE] = true
                    return null
                }
            }

            val turl = getSearchUrl(queryText, pageNum)
            Logger.i(LOG_TAG, "Search: $turl")
            html = getHtml(turl)
            if (html.isEmpty()) {
                return null
            }
            if (totalNum == 0) {
                totalNum = getSearchTotalNum(html)
            }
            state[STATE_TOTAL_PAGE_NUM_THIS_KEY] = totalNum
            return getSearchList(html)
        } else {
            return null
        }
    }

    protected open fun getSearchTotalNum(html: String): Int {
        return 0
    }

    protected open fun getSearchUrl(queryText: String, pageNum: Int): String {
        return String.format(WEB_SEARCH_URL, queryText, pageNum)
    }

    protected open fun getSearchList(html: String): List<TitleAndUrl>? {
        return null
    }
/*Searching Manga*/

    /*AllManga*/
    open fun getAllMangaList(state: HashMap<String, Any>): List<TitleAndUrl>? {
        var noMore = false
        if (state.containsKey(STATE_NO_MORE)) {
            noMore = state[STATE_NO_MORE] as Boolean
        }

        if (!noMore) {
            var pageNum = 1
            var totalNum = if (state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY))
                state[STATE_TOTAL_PAGE_NUM_THIS_KEY] as Int
            else
                0
            var html = ""
            //no total num means first time
            if (state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY)) {
                if (state.containsKey(STATE_PAGE_NUM_NOW)) {
                    pageNum = state[STATE_PAGE_NUM_NOW] as Int
                }
                if (pageNum + 1 <= totalNum) {
                    pageNum++
                    state[STATE_PAGE_NUM_NOW] = pageNum
                } else {
                    state[STATE_NO_MORE] = true
                    return null
                }
            }

            val turl = getAllMangaUrl(pageNum)
            Log.v(LOG_TAG, "Search: $turl")
            html = getHtml(turl)
            if (html.isEmpty()) {
                return null
            }
            if (totalNum == 0) {
                totalNum = getAllMangaTotalNum(html)
            }
            state[STATE_TOTAL_PAGE_NUM_THIS_KEY] = totalNum
            return getAllMangaList(html)
        } else {
            return null
        }
    }

    protected open fun getAllMangaList(html: String): List<TitleAndUrl>? {
        return null
    }

    protected open fun getAllMangaTotalNum(html: String): Int {
        return 0
    }

    protected open fun getAllMangaUrl(pageNum: Int): String {
        return String.format(WEB_ALL_MANGA_BASE_URL, pageNum)
    }
/*AllManga*/

    open fun getMenuCover(menu: MangaMenuItem): String {
        return menu.imagePath
    }

    open fun getLoaderType() = WEB

    //TODO should be remove later
    fun toMenuItem(pageList: List<TitleAndUrl>): List<MangaMenuItem> {
        val mangaList = mutableListOf<MangaMenuItem>()
        pageList.forEach {
            mangaList.add(MangaMenuItem(
                "Menu-$it",
                it.title,
                "",
                it.imagePath,
                it.url,
                mangaWebSource))
        }
        return mangaList
    }

    companion object {
        private val LOG_TAG = "MangaProvider"
        private val HTTP_TIMEOUT_NUM = 10000

        val STATE_SEARCH_QUERYTEXT = "key_search_querytext"
        val STATE_PAGE_KEY = "key_page_key"
        val STATE_PAGE_NUM_NOW = "key_page_num_now"
        val STATE_TOTAL_PAGE_NUM_THIS_KEY = "key_total_page_num"
        val STATE_NO_MORE = "key_no_more"
    }
}
