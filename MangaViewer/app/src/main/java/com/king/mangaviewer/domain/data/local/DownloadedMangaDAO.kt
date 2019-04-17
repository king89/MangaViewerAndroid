package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
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

    @Query("SELECT * FROM downloaded_manga where chapter_hash = :chapterHash")
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