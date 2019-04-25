package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface DownloadedMangaDataSource {
  fun checkIsDownloaded(chapterHash: String): Single<Boolean>
  fun addDownloadedManga(manga: DownloadedManga): Completable
  fun removeDownloadedMangaByMenu(menuHash: String): Completable
  fun removeDownloadedMangaByChapter(chapterHash: String): Completable
  fun getMangaMenuList(): Single<List<MangaMenuItem>>
  fun getMangaChapterList(menu: MangaMenuItem): Single<List<MangaChapterItem>>
}

class DownloadedMangaDataSourceImpl @Inject constructor(
    private val downloadedMangaDAO: DownloadedMangaDAO
) : DownloadedMangaDataSource {
  override fun checkIsDownloaded(chapterHash: String): Single<Boolean> {
    return downloadedMangaDAO.getChapterList(chapterHash).map { it.any() }
  }

  override fun addDownloadedManga(manga: DownloadedManga): Completable {
    return Completable.fromAction {
      downloadedMangaDAO.insert(manga)
    }
  }

  override fun removeDownloadedMangaByMenu(menuHash: String): Completable {
    return Completable.fromAction {
      val list = downloadedMangaDAO.getChapterList(menuHash).blockingGet()
      downloadedMangaDAO.delete(list)
    }
  }

  override fun removeDownloadedMangaByChapter(chapterHash: String): Completable {
    return Completable.fromAction {
      val item = downloadedMangaDAO.getChapterByHash(chapterHash).blockingGet()
      downloadedMangaDAO.delete(item)
    }
  }

  override fun getMangaMenuList(): Single<List<MangaMenuItem>> {
    return downloadedMangaDAO.getMenuList()
        .toObservable()
        .flatMapIterable { it }
        .map { it.toMangaMenu() }
        .toList()
  }

  override fun getMangaChapterList(menu: MangaMenuItem): Single<List<MangaChapterItem>> {
    return downloadedMangaDAO.getChapterList(menu.hash)
        .toObservable()
        .flatMapIterable { it }
        .map { it.toChapterItem(menu) }
        .toList()
  }


  private fun DownloadedManga.toMangaMenu(): MangaMenuItem {
    val imagePath = ""
    return MangaMenuItem(menuHash, title, description, imagePath, menuUrl, MangaWebSource.DOWNLOAD)
  }

  private fun DownloadedManga.toChapterItem(menu: MangaMenuItem): MangaChapterItem {
    return MangaChapterItem(chapterHash, chapterName, description, "", chapterUrl, menu)
  }
}


