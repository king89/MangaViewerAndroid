package com.king.mangaviewer.ui.main

import com.king.mangaviewer.base.BaseActivityModule
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.di.annotation.ActivityViewModelKey
import com.king.mangaviewer.di.annotation.FragmentScoped
import com.king.mangaviewer.ui.main.fragment.AllMangaFragment
import com.king.mangaviewer.ui.main.fragment.FavouriteFragment
import com.king.mangaviewer.ui.main.fragment.FavouriteFragmentModule
import com.king.mangaviewer.ui.main.fragment.HomeFragment
import com.king.mangaviewer.ui.main.fragment.HomeFragmentModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [BaseActivityModule::class])
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

    @Binds
    @IntoMap
    @ActivityScoped
    @ActivityViewModelKey(MainActivityViewModel::class)
    abstract fun provideMainActivityViewModel(
            viewModel: MainActivityViewModel): BaseActivityViewModel


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