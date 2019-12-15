package com.king.mangaviewer.di

import android.arch.persistence.room.Room
import android.content.Context
import com.king.mangaviewer.BuildConfig
import com.king.mangaviewer.di.annotation.ApplicationScope
import com.king.mangaviewer.domain.data.local.DownloadedMangaDAO
import com.king.mangaviewer.domain.data.local.DownloadedMangaDataSource
import com.king.mangaviewer.domain.data.local.DownloadedMangaDataSourceImpl
import com.king.mangaviewer.domain.data.local.FavouriteMangaDAO
import com.king.mangaviewer.domain.data.local.FavouriteMangaDataSource
import com.king.mangaviewer.domain.data.local.FavouriteMangaLocalDataSource
import com.king.mangaviewer.domain.data.local.HistoryMangaDAO
import com.king.mangaviewer.domain.data.local.HistoryMangaDataSource
import com.king.mangaviewer.domain.data.local.HistoryMangaLocalDataSource
import com.king.mangaviewer.domain.data.local.LocalMangaDAO
import com.king.mangaviewer.domain.data.local.MangaDataBase
import com.king.mangaviewer.domain.data.local.MangaDataBase.Companion.DATABASE_NAME
import com.king.mangaviewer.domain.external.mangaprovider.ProviderFactory
import com.king.mangaviewer.domain.external.mangaprovider.ProviderFactoryImpl
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.repository.AppRepositoryImpl
import com.king.mangaviewer.domain.repository.DownloadTaskRepository
import com.king.mangaviewer.domain.repository.DownloadTaskRepositoryImpl
import com.king.mangaviewer.domain.repository.DownloadedMangaRepository
import com.king.mangaviewer.domain.repository.DownloadedMangaRepositoryImpl
import com.king.mangaviewer.domain.repository.FavoriteMangaRepository
import com.king.mangaviewer.domain.repository.FavoriteMangaRepositoryImpl
import com.king.mangaviewer.domain.repository.HistoryMangaRepository
import com.king.mangaviewer.domain.repository.HistoryMangaRepositoryImpl
import com.king.mangaviewer.domain.repository.LocalMangaRepository
import com.king.mangaviewer.domain.repository.LocalMangaRepositoryImpl
import com.king.mangaviewer.viewmodel.AppViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
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

    @ApplicationScope
    @Binds
    abstract fun provideDownloadedMangaDataSource(
        impl: DownloadedMangaDataSourceImpl): DownloadedMangaDataSource

    @ApplicationScope
    @Binds
    abstract fun provideDownloadedMangaRepository(
        impl: DownloadedMangaRepositoryImpl): DownloadedMangaRepository

    @ApplicationScope
    @Binds
    abstract fun provideDownloadTaskRepository(
        impl: DownloadTaskRepositoryImpl): DownloadTaskRepository

    @ApplicationScope
    @Binds
    abstract fun provideLocalMangaRepository(
        impl: LocalMangaRepositoryImpl): LocalMangaRepository

    @Module
    companion object {
        @ApplicationScope
        @Provides
        @JvmStatic
        fun provideDb(context: Context): MangaDataBase {
            return Room.databaseBuilder(
                context,
                MangaDataBase::class.java,
                DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addMigrations(MangaDataBase.MIGRATION_2_3)
                .addMigrations(MangaDataBase.MIGRATION_3_4)
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
        fun provideDownloadedMangaDao(db: MangaDataBase): DownloadedMangaDAO {
            return db.downloadedMangaDAO()
        }

        @ApplicationScope
        @Provides
        @JvmStatic
        fun provideLocalMangaDao(db: MangaDataBase): LocalMangaDAO {
            return db.localMangaDAO()
        }


        @ApplicationScope
        @Provides
        @JvmStatic
        fun providerOkHttpClient(): OkHttpClient {
            val timeout = 15L
            val logging = HttpLoggingInterceptor()
            logging.level = Level.BASIC
            return OkHttpClient().newBuilder()
                .addInterceptor {
                    if (BuildConfig.DEBUG) {
                        logging.intercept(it)
                    } else {
                        it.proceed(it.request())
                    }
                }
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
            }
        }
    }
}