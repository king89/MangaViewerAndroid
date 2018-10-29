package com.king.mangaviewer.di

import android.app.Application
import android.content.Context
import com.king.mangaviewer.di.annotation.ApplicationScope
import com.king.mangaviewer.viewmodel.AppViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides

/**
 * This is a Dagger module. We use this to bind our Application class as a Context in the AppComponent
 * By using Dagger Android we do not need to pass our Application instance to any module,
 * we simply need to expose our Application as Context.
 * One of the advantages of Dagger.Android is that your
 * Application & Activities are provided into your graph for you.
 * [ ].
 */
@Module
abstract class ApplicationModule {
    //expose Application as an injectable context
    @Binds
    internal abstract fun bindContext(application: Application): Context

    @Module
    companion object {
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