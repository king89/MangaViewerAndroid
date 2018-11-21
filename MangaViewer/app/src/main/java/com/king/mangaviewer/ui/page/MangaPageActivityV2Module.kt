package com.king.mangaviewer.ui.page

import com.king.mangaviewer.base.BaseActivityModule
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.base.BaseFragmentModule
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.di.annotation.ActivityViewModelKey
import com.king.mangaviewer.di.annotation.FragmentScoped
import com.king.mangaviewer.ui.page.fragment.ReaderFragment
import com.king.mangaviewer.ui.page.fragment.RtlViewPagerReaderFragment
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MangaPageActivityV2Module : BaseActivityModule(){

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun readerFragment(): ReaderFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun viewPagerReaderFragment(): ViewPagerReaderFragment

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun rtlViewPagerReaderFragment(): RtlViewPagerReaderFragment


    @Binds
    @IntoMap
    @ActivityScoped
    @ActivityViewModelKey(MangaPageActivityV2ViewModel::class)
    abstract fun provideViewModel(
            viewModel: MangaPageActivityV2ViewModel): BaseActivityViewModel

}