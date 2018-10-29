package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [
    FavouriteManga::class,
    HistoryManga::class],
        version = 1)
abstract class MangaDataBase : RoomDatabase() {
    abstract fun favourtieMangaDAO(): FavouriteMangaDAO
    abstract fun historyMangaDAO(): HistoryMangaDAO

}