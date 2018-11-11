package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Observable
import io.reactivex.Single

@Dao
interface HistoryMangaDAO {

    @Query("SELECT * FROM history_manga order by last_read_time desc")
    fun getList(): Single<List<HistoryManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: HistoryManga)

    @Delete
    fun delete(item: HistoryManga)

    @Query("DELETE FROM history_manga")
    fun deleteAll()

    @Query("select * from history_manga where hash = :hash")
    fun getItem(hash: String): Single<HistoryManga>

}