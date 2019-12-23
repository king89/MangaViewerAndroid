package com.king.mangaviewer.domain.data.local

import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.AppViewModel
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

interface LocalMangaDataSource {
    fun addManga(manga: MangaMenuItem): Completable
    fun removeManga(manga: MangaMenuItem): Completable
    fun getMangaList(): Single<List<MangaMenuItem>>
}

class LocalMangaLocalDataSource @Inject constructor(
    private val appViewModel: AppViewModel,
    private val dao: LocalMangaDAO) : LocalMangaDataSource {
    override fun addManga(manga: MangaMenuItem): Completable {
        return Completable.fromCallable {
            val item = LocalManga(manga)
            dao.insert(item)
        }
    }

    override fun removeManga(manga: MangaMenuItem): Completable {
        return Completable.fromCallable {
            dao.delete(LocalManga(manga))
        }
    }

    override fun getMangaList(): Single<List<MangaMenuItem>> {
        return dao.getList().toObservable()
            .flatMapIterable { it }
            .map {
                it.toMangaMenuItem()
            }
            .toList()
    }

    private fun LocalManga.toMangaMenuItem(): MangaMenuItem {
        return this.let {
            val mangaSource = appViewModel.Setting.mangaWebSources.firstOrNull { source ->
                source.id == manga_websource_id
            }!!
            MangaMenuItem(hash, title, description, imagePath, url, mangaSource)
        }
    }

}