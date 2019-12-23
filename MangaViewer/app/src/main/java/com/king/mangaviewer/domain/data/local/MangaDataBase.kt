package com.king.mangaviewer.domain.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [
    FavouriteManga::class,
    HistoryManga::class,
    DownloadedManga::class,
    LocalManga::class],
    version = 4,
    exportSchema = true)
abstract class MangaDataBase : RoomDatabase() {
    abstract fun favouriteMangaDAO(): FavouriteMangaDAO
    abstract fun historyMangaDAO(): HistoryMangaDAO
    abstract fun downloadedMangaDAO(): DownloadedMangaDAO
    abstract fun localMangaDAO(): LocalMangaDAO

    companion object {
        const val DATABASE_NAME = "manga.db"
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val sql = """CREATE TABLE IF NOT EXISTS `downloaded_manga` (`hash` TEXT NOT NULL, `description` TEXT NOT NULL, `title` TEXT NOT NULL, `url` TEXT NOT NULL, `created_date_time` TEXT NOT NULL, `origin_hash` TEXT NOT NULL, `menu_hash` TEXT NOT NULL, `menu_title` TEXT NOT NULL, `menu_description` TEXT NOT NULL, `menu_cover_image_base64` TEXT NOT NULL, `menu_url` TEXT NOT NULL, `menu_genre` TEXT NOT NULL, `menu_origin_hash` TEXT NOT NULL, PRIMARY KEY(`hash`))""".trimMargin()
                database.execSQL(sql)
            }

        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val sql = """CREATE TABLE IF NOT EXISTS `local_manga` (`hash` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `imagePath` TEXT NOT NULL, `url` TEXT NOT NULL, `manga_websource_id` INTEGER NOT NULL, `updated_date` TEXT NOT NULL, PRIMARY KEY(`hash`))""".trimMargin()
                database.execSQL(sql)
            }

        }
    }
}