package com.king.mangaviewer.di

import android.app.Activity
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.util.AppNavigator
import dagger.Module
import dagger.Provides

@Module
class NavigatorModule {
    @Provides
    @ActivityScoped
    fun provideNavigtor(activity: Activity): AppNavigator {
        return AppNavigator(activity)
    }
}