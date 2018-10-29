package com.king.mangaviewer.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.king.mangaviewer.domain.data.local.FavouriteMangaDAO
import com.king.mangaviewer.domain.data.local.HistoryMangaDAO
import com.king.mangaviewer.domain.data.local.MangaDataBase
import com.king.mangaviewer.di.annotation.ApplicationScope
import com.king.mangaviewer.viewmodel.AppViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Singleton

/*
Can add this class in different flavor
 */
@Module
abstract class RepositoryModule {

    @Module
    companion object {
        @Singleton
        @Provides
        @JvmStatic
        fun provideDb(context: Application): MangaDataBase {
            return Room.databaseBuilder(
                    context.applicationContext,
                    MangaDataBase::class.java,
                    "manga.db").build()
        }

        @Singleton
        @Provides
        @JvmStatic
        fun provideFavouriteMangaDao(db: MangaDataBase): FavouriteMangaDAO {
            return db.favourtieMangaDAO()
        }

        @Singleton
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

            }
        }
    }
}