package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.StringUtils
import com.king.mangaviewer.util.getMangaWebSource
import org.junit.Assert.assertEquals
import org.junit.Test

class DownloadedMangaTest {

    @Test
    fun testCreateFromTask() {
        val menu = MangaMenuItem("id", "title", "description", "http://a.com/123.jpg",
            "http://a.com/456",
            getMangaWebSource())
        val chapter = MangaChapterItem("id", "title 001", "description", "imagePath",
            "http://a.com/456/aa", menu)

        val task = DownloadTask(chapter)
        val folder = "/manga/1234"
        val fileUri = "/manga/1234/321.zip"
        val downloadedManga = DownloadedManga.createFromTask(task, folder, fileUri)
        println("downloadedManga: $downloadedManga")

        val newMenuHash = StringUtils.getHash(folder)
        val newChapterHash = StringUtils.getHash(fileUri)

        assertEquals(newMenuHash, downloadedManga.menu.hash)
        assertEquals(newChapterHash, downloadedManga.hash)

        assertEquals(menu.hash, downloadedManga.menu.originHash)
        assertEquals(chapter.hash, downloadedManga.originHash)


    }
}