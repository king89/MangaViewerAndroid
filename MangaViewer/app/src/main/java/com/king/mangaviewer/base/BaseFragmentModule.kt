package com.king.mangaviewer.base

import androidx.lifecycle.ViewModel
import com.king.mangaviewer.di.annotation.FragmentScoped
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import dagger.Binds
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider

@Module
abstract class BaseFragmentModule {
    @Binds
    @FragmentScoped
    @FragmentScopedFactory
    abstract fun bindViewModelFactory(viewModelFactory: FragmentViewModelFactory): ViewModelFactory
}

@FragmentScoped
@Suppress("UNCHECKED_CAST")
class FragmentViewModelFactory @Inject constructor(
        viewModel: MutableMap<Class<out BaseFragmentViewModel>, Provider<BaseFragmentViewModel>>
) : ViewModelFactory(viewModel as Map<Class<out ViewModel>, Provider<ViewModel>>)