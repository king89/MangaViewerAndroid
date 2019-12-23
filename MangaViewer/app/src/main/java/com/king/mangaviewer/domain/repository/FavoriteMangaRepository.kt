package com.king.mangaviewer.domain.repository

import com.king.mangaviewer.domain.data.local.FavouriteMangaDataSource
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

//use for manga local data and remote data

interface FavoriteMangaRepository {
    fun checkIsFavorite(manga: MangaMenuItem?): Single<Boolean>
    fun addFavouriteManga(manga: MangaMenuItem, chapterCount: Int): Completable
    fun removeFavouriteManga(manga: MangaMenuItem): Completable
    fun getFavouriteMangaList(): Single<List<FavouriteMangaMenuItem>>
    fun updateFavouriteManga(manga: MangaMenuItem, chapterCount: Int): Completable
    fun updateAllHash(): Completable
}

class FavoriteMangaRepositoryImpl @Inject constructor(
    private val favoriteMangaDataSource: FavouriteMangaDataSource) : FavoriteMangaRepository {
    override fun updateAllHash(): Completable = favoriteMangaDataSource.updateAllHash()

    override fun checkIsFavorite(
        manga: MangaMenuItem?): Single<Boolean> = favoriteMangaDataSource.checkIsFavorite(manga)

    override fun addFavouriteManga(manga: MangaMenuItem,
        chapterCount: Int): Completable = favoriteMangaDataSource.addFavouriteManga(manga,
        chapterCount)

    override fun removeFavouriteManga(
        manga: MangaMenuItem): Completable = favoriteMangaDataSource.removeFavouriteManga(manga)

    override fun getFavouriteMangaList(): Single<List<FavouriteMangaMenuItem>> = favoriteMangaDataSource.getFavouriteMangaList()

    override fun updateFavouriteManga(manga: MangaMenuItem,
        chapterCount: Int): Completable = favoriteMangaDataSource.updateFavouriteManga(manga,
        chapterCount)

}