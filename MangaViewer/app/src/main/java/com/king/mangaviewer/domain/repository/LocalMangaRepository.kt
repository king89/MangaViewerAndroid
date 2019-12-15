package com.king.mangaviewer.domain.repository

import com.king.mangaviewer.domain.data.local.LocalMangaDataSource
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

//use for manga local data and remote data

interface LocalMangaRepository {
    fun addManga(manga: MangaMenuItem): Completable
    fun removeManga(manga: MangaMenuItem): Completable
    fun getMangaList(): Single<List<MangaMenuItem>>
}

class LocalMangaRepositoryImpl @Inject constructor(
    private val dataSource: LocalMangaDataSource) : LocalMangaRepository {
    override fun addManga(manga: MangaMenuItem): Completable = dataSource.addManga(manga)
    override fun removeManga(manga: MangaMenuItem): Completable = dataSource.removeManga(manga)
    override fun getMangaList(): Single<List<MangaMenuItem>> = dataSource.getMangaList()

}