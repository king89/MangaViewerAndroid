package com.king.mangaviewer.ui.main.fragment

import com.king.mangaviewer.base.BaseFragmentModule
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.di.annotation.FragmentScoped
import com.king.mangaviewer.di.annotation.FragmentViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [BaseFragmentModule::class])
abstract class LocalFragmentModule {

    @Binds
    @IntoMap
    @FragmentScoped
    @FragmentViewModelKey(LocalFragmentViewModel::class)
    abstract fun provideLocalFragmentViewModel(
            viewModel: LocalFragmentViewModel): BaseFragmentViewModel
}