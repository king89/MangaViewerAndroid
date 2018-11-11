package com.king.mangaviewer.domain.data

import com.king.mangaviewer.domain.data.local.FavouriteMangaDataSource
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

//use for manga local data and remote data

interface FavoriteMangaRepository : FavouriteMangaDataSource {

}

class FavoriteMangaRepositoryImpl @Inject constructor(
        private val favoriteMangaDataSource: FavouriteMangaDataSource) : FavoriteMangaRepository {

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