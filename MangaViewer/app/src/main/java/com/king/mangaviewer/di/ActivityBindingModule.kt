package com.king.mangaviewer.di

import com.king.mangaviewer.activity.MangaPageActivity
import com.king.mangaviewer.ui.main.MainActivity
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import com.king.mangaviewer.ui.chapter.MangaChapterActivityModule
import com.king.mangaviewer.ui.main.MainActivityModule
import com.king.mangaviewer.ui.page.MangaPageActivityV2
import com.king.mangaviewer.ui.search.SearchResultActivity
import com.king.mangaviewer.ui.setting.SettingsActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MangaChapterActivityModule::class])
    internal abstract fun mangaChapterActivity(): MangaChapterActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun mangaPageActivityV2(): MangaPageActivityV2

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun mangaPageActivity(): MangaPageActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun settingsActivity(): SettingsActivity

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun searchResultActivity(): SearchResultActivity
    //Add Activity Module to the map
}
