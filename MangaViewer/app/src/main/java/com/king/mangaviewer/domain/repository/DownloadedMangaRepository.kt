package com.king.mangaviewer.domain.repository

import com.king.mangaviewer.domain.data.local.DownloadedManga
import com.king.mangaviewer.domain.data.local.DownloadedMangaDataSource
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface DownloadedMangaRepository : DownloadedMangaDataSource {

}

class DownloadedMangaRepositoryImpl @Inject constructor(
    private val dataSource: DownloadedMangaDataSource

) : DownloadedMangaRepository {
  override fun checkIsDownloaded(
      chapterHash: String): Single<Boolean> = dataSource.checkIsDownloaded(chapterHash)

  override fun addDownloadedManga(
      manga: DownloadedManga): Completable = dataSource.addDownloadedManga(manga)

  override fun removeDownloadedMangaByMenu(
      menuHash: String): Completable = dataSource.removeDownloadedMangaByMenu(menuHash)

  override fun removeDownloadedMangaByChapter(
      chapterHash: String): Completable = dataSource.removeDownloadedMangaByChapter(chapterHash)

  override fun getMangaMenuList(): Single<List<DownloadedManga>> = dataSource.getMangaMenuList()

  override fun getMangaChapterList(
      menu: MangaMenuItem): Single<List<DownloadedManga>> = dataSource.getMangaChapterList(menu)

}