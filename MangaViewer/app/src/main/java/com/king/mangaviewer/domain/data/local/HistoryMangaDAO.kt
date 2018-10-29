package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Single

@Dao
interface HistoryMangaDAO {

    @Query("SELECT * FROM favourite_manga")
    fun getList(): Single<List<HistoryManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: HistoryManga)

    @Update
    fun update(item: HistoryManga): Int

}