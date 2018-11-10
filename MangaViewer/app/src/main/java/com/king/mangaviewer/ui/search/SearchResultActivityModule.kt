package com.king.mangaviewer.ui.search

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
abstract class SearchResultActivityModule {

    @Binds
    @IntoMap
    @ActivityScoped
    @ActivityViewModelKey(SearchResultActivityViewModel::class)
    abstract fun provideActivityViewModel(
            viewModel: SearchResultActivityViewModel): BaseActivityViewModel

}