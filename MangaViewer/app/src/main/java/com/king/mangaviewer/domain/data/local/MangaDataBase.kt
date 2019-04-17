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
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val sql = """CREATE TABLE IF NOT EXISTS `downloaded_manga` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `menu_hash` TEXT NOT NULL, `chapter_hash` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `chapter_name` TEXT NOT NULL, `menu_cover_image_base64` TEXT NOT NULL, `menu_url` TEXT NOT NULL, `chapter_url` TEXT NOT NULL, `genre` TEXT NOT NULL)""".trimMargin()
                database.execSQL(sql)
            }

        }
    }
}