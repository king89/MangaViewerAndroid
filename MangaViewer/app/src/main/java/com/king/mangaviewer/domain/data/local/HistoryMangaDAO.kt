package com.king.mangaviewer.domain.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single

@Dao
interface HistoryMangaDAO {

    @Query("SELECT * FROM history_manga order by last_read_time desc")
    fun getList(): Single<List<HistoryManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: HistoryManga)

    @Delete
    fun delete(item: HistoryManga)

    @Update
    fun update(item: HistoryManga)

    @Query("DELETE FROM history_manga")
    fun deleteAll()

    @Query("select * from history_manga where hash = :hash")
    fun getItem(hash: String): Single<HistoryManga>

    @Query("select * from history_manga where menu_hash = :menuHash")
    fun getLastReadItem(menuHash: String): Single<HistoryManga>

    @Query("select * from history_manga order by last_read_time desc limit 1")
    fun getLastReadItem(): Single<HistoryManga>

    @Query(
        "select * from (select * from history_manga order by last_read_time desc) group by menu_hash order by last_read_time desc")
    fun getLastReadMangaItem(): Single<List<HistoryManga>>
}