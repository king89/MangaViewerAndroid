package com.king.mangaviewer.model

import com.king.mangaviewer.domain.external.mangaprovider.DownloadedMangaProvider
import com.king.mangaviewer.domain.external.mangaprovider.LocalMangaProvider

/**
 * Created by KinG on 8/13/2015.
 */
class MangaWebSource(val id: Int,
    val name: String,
    val displayName: String,
    val className: String,
    val order: Int,
    val language: String,
    var enable: Int) : Comparable<MangaWebSource> {

  override fun compareTo(another: MangaWebSource): Int {
    return when{
      this.order >= 0 && another.order >= 0 -> {
        if (this.order < another.order) {
          -1
        } else {
          1
        }
      }
      this.order < 0 && another.order < 0 -> {
        if (this.order > another.order) {
          -1
        } else {
          1
        }
      }
      else -> {
        if (this.order > another.order) {
          -1
        } else {
          1
        }
      }
    }


  }

  companion object {
    val LOCAL = MangaWebSource(-1, "LocalManga", "LocalManga",
        LocalMangaProvider::class.java.name,
        -1, "en", 0)
    val DOWNLOAD = MangaWebSource(-2, "DownloadManga", "DownloadManga",
        DownloadedMangaProvider::class.java.name, -2, "en", 0)

  }

}
