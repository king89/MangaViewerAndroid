package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface FavouriteMangaDataSource {
    fun checkIsFavorite(manga: MangaMenuItem?): Single<Boolean>
    fun addFavouriteManga(manga: MangaMenuItem, chapterCount: Int): Completable
    fun removeFavouriteManga(manga: MangaMenuItem): Completable
    fun getFavouriteMangaList(): Single<List<FavouriteMangaMenuItem>>
    fun updateFavouriteManga(manga: MangaMenuItem, chapterCount: Int): Completable
}

class FavouriteMangaLocalDataSource @Inject constructor(
        private val appViewModel: AppViewModel,
        private val favouriteMangaDAO: FavouriteMangaDAO) : FavouriteMangaDataSource {

    override fun checkIsFavorite(manga: MangaMenuItem?): Single<Boolean> {
        if (manga == null) return Single.just(false)
        return favouriteMangaDAO.getFavouriteByHash(manga.hash).map { it.any() }
    }

    override fun addFavouriteManga(manga: MangaMenuItem, chapterCount: Int): Completable {
        return Completable.fromCallable {
            val item = FavouriteManga(FavouriteMangaMenuItem(manga, chapterCount))
            favouriteMangaDAO.insert(item)
        }
    }

    override fun removeFavouriteManga(manga: MangaMenuItem): Completable {
        return Completable.fromCallable {
            val item = favouriteMangaDAO.getFavouriteByHash(manga.hash).blockingGet().firstOrNull()
            item?.run {
                favouriteMangaDAO.delete(item)
            }
        }
    }

    override fun getFavouriteMangaList() =
            favouriteMangaDAO.getFavouriteList()
                    .toObservable()
                    .flatMapIterable {
                        it
                    }
                    .map {
                        it.toFavouriteMangaItem()
                    }
                    .toList()!!

    override fun updateFavouriteManga(manga: MangaMenuItem, chapterCount: Int): Completable {
        return removeFavouriteManga(manga)
                .andThen(addFavouriteManga(manga, chapterCount))
    }

    private fun FavouriteManga.toFavouriteMangaItem(): FavouriteMangaMenuItem {
        return this.let {
            val mangaSource = appViewModel.Setting.mangaWebSources.firstOrNull { source ->
                source.id == manga_websource_id
            }
            val item = MangaMenuItem(hash, title, description, imagePath, url, mangaSource)
            FavouriteMangaMenuItem.createFavouriteMangaMenuItem(item, favourite_date, updated_date,
                    chapter_count, update_count)
        }
    }
}