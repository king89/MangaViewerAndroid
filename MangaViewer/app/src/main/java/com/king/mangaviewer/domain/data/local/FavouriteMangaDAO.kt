package com.king.mangaviewer.domain.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import io.reactivex.Single

@Dao
interface FavouriteMangaDAO {
    /**
     * Select all items from the items table.
     *
     * @return all items.
     */
    @Query("SELECT * FROM favourite_manga")
    fun getFavouriteList(): Single<List<FavouriteManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: FavouriteManga)

    @Query("select * from favourite_manga where hash = :hash")
    fun getFavouriteByHash(hash: String): Single<List<FavouriteManga>>

    /**
     * Update a task.
     *
     * @param task task to be updated
     * @return the number of tasks updated. This should always be 1.
     */
    @Update
    fun update(item: FavouriteManga): Int

    @Delete
    fun delete(item: FavouriteManga)

}