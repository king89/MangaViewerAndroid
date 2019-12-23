package com.king.mangaviewer.di

import android.app.Application
import com.king.mangaviewer.MyApplication
import com.king.mangaviewer.di.annotation.ApplicationScope
import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.external.mangaprovider.ProviderFactory
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import okhttp3.OkHttpClient
import javax.inject.Provider

@Component(modules = [
    ApplicationModule::class,
    ActivityBindingModule::class,
    RepositoryModule::class,
    MangaProviderModule::class,
    AndroidSupportInjectionModule::class
])
@ApplicationScope
interface AppComponent : AndroidInjector<MyApplication> {

    // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
    // never having to instantiate any modules or say which module we are passing the application to.
    // Application will just be provided into our app graph now.
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): AppComponent.Builder

        fun build(): AppComponent
    }

    fun okHttpClient(): OkHttpClient

    fun providerFactory(): ProviderFactory

    fun appRepository(): AppRepository

    fun mangaProviderMap(): MutableMap<Class<out MangaProvider>, Provider<MangaProvider>>
}
