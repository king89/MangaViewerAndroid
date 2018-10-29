package com.king.mangaviewer.di

import com.king.mangaviewer.ui.main.MainActivity
import com.king.mangaviewer.di.annotation.ActivityScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity

    //Add Activity Module to the map
}
