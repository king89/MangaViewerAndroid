package com.king.mangaviewer.di

import android.arch.persistence.room.Room
import android.content.Context
import com.king.mangaviewer.domain.data.local.FavouriteMangaDAO
import com.king.mangaviewer.domain.data.local.HistoryMangaDAO
import com.king.mangaviewer.domain.data.local.MangaDataBase
import com.king.mangaviewer.di.annotation.ApplicationScope
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.data.AppRepositoryImpl
import com.king.mangaviewer.domain.data.FavoriteMangaRepository
import com.king.mangaviewer.domain.data.FavoriteMangaRepositoryImpl
import com.king.mangaviewer.domain.data.HistoryMangaRepository
import com.king.mangaviewer.domain.data.HistoryMangaRepositoryImpl
import com.king.mangaviewer.domain.data.local.FavouriteMangaDataSource
import com.king.mangaviewer.domain.data.local.FavouriteMangaLocalDataSource
import com.king.mangaviewer.domain.data.local.HistoryMangaDataSource
import com.king.mangaviewer.domain.data.local.HistoryMangaLocalDataSource
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactory
import com.king.mangaviewer.domain.data.mangaprovider.ProviderFactoryImpl
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.viewmodel.AppViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit.SECONDS

/*
Can add this class in different flavor
 */
@Module
abstract class RepositoryModule {
    @ApplicationScope
    @Binds
    abstract fun provideMangaProviderFactory(impl: ProviderFactoryImpl): ProviderFactory

    @ApplicationScope
    @Binds
    abstract fun provideAppRepository(impl: AppRepositoryImpl): AppRepository

    @ApplicationScope
    @Binds
    abstract fun provideHistoryMangaRepository(
            impl: HistoryMangaRepositoryImpl): HistoryMangaRepository

    @ApplicationScope
    @Binds
    abstract fun provideHistoryMangaDataSource(
            impl: HistoryMangaLocalDataSource): HistoryMangaDataSource

    @ApplicationScope
    @Binds
    abstract fun provideFavoriteMangaDataSource(
            impl: FavouriteMangaLocalDataSource): FavouriteMangaDataSource

    @ApplicationScope
    @Binds
    abstract fun provideFavoriteMangaRepository(
            impl: FavoriteMangaRepositoryImpl): FavoriteMangaRepository

    @Module
    companion object {
        @ApplicationScope
        @Provides
        @JvmStatic
        fun provideDb(context: Context): MangaDataBase {
            return Room.databaseBuilder(
                    context,
                    MangaDataBase::class.java,
                    "manga.db")
                    .fallbackToDestructiveMigration()
                    .build()
        }

        @ApplicationScope
        @Provides
        @JvmStatic
        fun provideFavouriteMangaDao(db: MangaDataBase): FavouriteMangaDAO {
            return db.favouriteMangaDAO()
        }

        @ApplicationScope
        @Provides
        @JvmStatic
        fun provideHistoryMangaDao(db: MangaDataBase): HistoryMangaDAO {
            return db.historyMangaDAO()
        }

        @ApplicationScope
        @Provides
        @JvmStatic
        fun providerOkHttpClient(): OkHttpClient {
            val timeout = 15L
            return OkHttpClient().newBuilder()
                    .connectTimeout(timeout, SECONDS)
                    .readTimeout(timeout, SECONDS)
                    .writeTimeout(timeout, SECONDS)
                    .build()
        }

        @Provides
        @JvmStatic
        @ApplicationScope
        fun provideAppViewModel(application: Context): AppViewModel {
            return AppViewModel(application).apply {
                Setting = SettingViewModel.loadSetting(application)
                Logger.d("-=-=", "create AppViewModel ")
            }
        }
    }
}