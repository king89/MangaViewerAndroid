package com.king.mangaviewer.domain.data.local

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration

@Database(entities = [
  FavouriteManga::class,
  HistoryManga::class,
  DownloadedManga::class],
    version = 3,
    exportSchema = true)
abstract class MangaDataBase : RoomDatabase() {
  abstract fun favouriteMangaDAO(): FavouriteMangaDAO
  abstract fun historyMangaDAO(): HistoryMangaDAO
  abstract fun downloadedMangaDAO(): DownloadedMangaDAO

  companion object {
    const val DATABASE_NAME = "manga.db"
    val MIGRATION_2_3 = object : Migration(2, 3) {
      override fun migrate(database: SupportSQLiteDatabase) {
        val sql = """CREATE TABLE IF NOT EXISTS `downloaded_manga` (`hash` TEXT NOT NULL, `description` TEXT NOT NULL, `title` TEXT NOT NULL, `url` TEXT NOT NULL, `created_date_time` TEXT NOT NULL, `origin_hash` TEXT NOT NULL, `menu_hash` TEXT NOT NULL, `menu_title` TEXT NOT NULL, `menu_description` TEXT NOT NULL, `menu_cover_image_base64` TEXT NOT NULL, `menu_url` TEXT NOT NULL, `menu_genre` TEXT NOT NULL, `menu_origin_hash` TEXT NOT NULL, PRIMARY KEY(`hash`))""".trimMargin()
        database.execSQL(sql)
      }

    }
  }
}