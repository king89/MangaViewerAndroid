package com.king.mangaviewer.ui.chapter

import com.king.mangaviewer.base.BaseActivityModule
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.di.annotation.ActivityViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MangaChapterActivityModule : BaseActivityModule(){

    @Binds
    @IntoMap
    @ActivityScoped
    @ActivityViewModelKey(MangaChapterActivityViewModel::class)
    abstract fun provideViewModel(
            viewModel: MangaChapterActivityViewModel): BaseActivityViewModel

}