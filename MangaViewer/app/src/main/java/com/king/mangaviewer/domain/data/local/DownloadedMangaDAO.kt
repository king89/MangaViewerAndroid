package com.king.mangaviewer.domain.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single

@Dao
interface DownloadedMangaDAO {
    /**
     * Select all items from the items table.
     *
     * @return all items.
     */
    @Query("SELECT * FROM downloaded_manga group by menu_hash")
    fun getMenuList(): Single<List<DownloadedManga>>

    @Query("SELECT * FROM downloaded_manga where menu_hash = :hash")
    fun getMenuByHash(hash: String): Single<DownloadedManga>

    @Query("SELECT * FROM downloaded_manga where hash = :chapterHash")
    fun getChapterByHash(chapterHash: String): Single<DownloadedManga>

    @Query("SELECT * FROM downloaded_manga where menu_hash = :menuHash")
    fun getChapterList(menuHash: String): Single<List<DownloadedManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: DownloadedManga)

    @Update
    fun update(item: DownloadedManga): Int

    @Delete
    fun delete(item: List<DownloadedManga>)

    @Delete
    fun delete(item: DownloadedManga)
}