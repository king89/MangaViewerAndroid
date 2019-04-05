package com.king.mangaviewer.ui.main

import android.app.Activity
import com.king.mangaviewer.base.BaseActivityModule
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.di.NavigatorModule
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.di.annotation.ActivityViewModelKey
import com.king.mangaviewer.di.annotation.FragmentScoped
import com.king.mangaviewer.ui.main.fragment.AllMangaFragment
import com.king.mangaviewer.ui.main.fragment.FavouriteFragment
import com.king.mangaviewer.ui.main.fragment.FavouriteFragmentModule
import com.king.mangaviewer.ui.main.fragment.HistoryFragment
import com.king.mangaviewer.ui.main.fragment.HistoryFragmentModule
import com.king.mangaviewer.ui.main.fragment.HomeFragment
import com.king.mangaviewer.ui.main.fragment.HomeFragmentModule
import com.king.mangaviewer.ui.main.fragment.LocalFragment
import com.king.mangaviewer.ui.main.fragment.LocalFragmentModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [BaseActivityModule::class, NavigatorModule::class])
abstract class MainActivityModule {
    @FragmentScoped
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun homeFragment(): HomeFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [HomeFragmentModule::class])
    abstract fun allMangaFragment(): AllMangaFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [FavouriteFragmentModule::class])
    abstract fun favoriteFragment(): FavouriteFragment

    @FragmentScoped
    @ContributesAndroidInjector(modules = [HistoryFragmentModule::class])
    abstract fun historyFragment(): HistoryFragment


    @FragmentScoped
    @ContributesAndroidInjector(modules = [LocalFragmentModule::class])
    abstract fun localFragment(): LocalFragment

    @Binds
    @IntoMap
    @ActivityScoped
    @ActivityViewModelKey(MainActivityViewModel::class)
    abstract fun provideMainActivityViewModel(
        viewModel: MainActivityViewModel): BaseActivityViewModel

    @Binds
    @ActivityScoped
    abstract fun provideActivity(activity: MainActivity): Activity

    @Module
    companion object {
        @Provides
        @ActivityScoped
        @JvmStatic
        fun provideString(): String {
            return "this is the injected string"
        }

    }

}