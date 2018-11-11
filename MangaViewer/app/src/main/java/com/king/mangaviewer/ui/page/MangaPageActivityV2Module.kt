package com.king.mangaviewer.ui.page

import com.king.mangaviewer.base.BaseActivityModule
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.base.BaseFragmentModule
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.di.annotation.ActivityViewModelKey
import com.king.mangaviewer.di.annotation.FragmentScoped
import com.king.mangaviewer.ui.page.fragment.ReaderFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MangaPageActivityV2Module : BaseActivityModule(){

    @Binds
    @IntoMap
    @ActivityScoped
    @ActivityViewModelKey(MangaPageActivityV2ViewModel::class)
    abstract fun provideViewModel(
            viewModel: MangaPageActivityV2ViewModel): BaseActivityViewModel

}