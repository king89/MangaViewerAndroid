package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [
    FavouriteManga::class,
    HistoryManga::class],
        version = 2)
abstract class MangaDataBase : RoomDatabase() {
    abstract fun favouriteMangaDAO(): FavouriteMangaDAO
    abstract fun historyMangaDAO(): HistoryMangaDAO

}