package com.king.mangaviewer.domain.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Single

@Dao
interface LocalMangaDAO {
    /**
     * Select all items from the items table.
     *
     * @return all items.
     */
    @Query("SELECT * FROM local_manga")
    fun getList(): Single<List<LocalManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: LocalManga)

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    fun update(item: LocalManga): Int

    @Delete
    fun delete(item: LocalManga)

}