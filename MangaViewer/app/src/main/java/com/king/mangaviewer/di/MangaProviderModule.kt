package com.king.mangaviewer.di

import com.king.mangaviewer.di.annotation.ApplicationScope
import com.king.mangaviewer.di.annotation.MangaProviderKey
import com.king.mangaviewer.domain.external.mangaprovider.DownloadedMangaProvider
import com.king.mangaviewer.domain.external.mangaprovider.LocalMangaProvider
import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.external.mangaprovider.WebDMZJ
import com.king.mangaviewer.domain.external.mangaprovider.WebMangaFox
import com.king.mangaviewer.domain.external.mangaprovider.WebMangaReader
import com.king.mangaviewer.domain.external.mangaprovider.WebManhuagui
import com.king.mangaviewer.domain.external.mangaprovider.WebTestManga
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MangaProviderModule {

    @Binds
    @IntoMap
    @MangaProviderKey(DownloadedMangaProvider::class)
    @ApplicationScope
    abstract fun downloadedMangaProvider(provider: DownloadedMangaProvider): MangaProvider

    @Binds
    @IntoMap
    @MangaProviderKey(LocalMangaProvider::class)
    @ApplicationScope
    abstract fun LocalMangaProvider(provider: LocalMangaProvider): MangaProvider

    @Binds
    @IntoMap
    @MangaProviderKey(WebMangaFox::class)
    @ApplicationScope
    abstract fun WebMangaFox(provider: WebMangaFox): MangaProvider

    @Binds
    @IntoMap
    @MangaProviderKey(WebManhuagui::class)
    @ApplicationScope
    abstract fun WebManhuagui(provider: WebManhuagui): MangaProvider

    @Binds
    @IntoMap
    @MangaProviderKey(WebMangaReader::class)
    @ApplicationScope
    abstract fun WebMangaReader(provider: WebMangaReader): MangaProvider

    @Binds
    @IntoMap
    @MangaProviderKey(WebDMZJ::class)
    @ApplicationScope
    abstract fun WebDMZJ(provider: WebDMZJ): MangaProvider

    @Binds
    @IntoMap
    @MangaProviderKey(WebTestManga::class)
    @ApplicationScope
    abstract fun WebTestManga(provider: WebTestManga): MangaProvider

}