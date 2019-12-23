package com.king.mangaviewer.base


import androidx.lifecycle.ViewModel
import com.king.mangaviewer.di.annotation.ActivityScoped
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
import dagger.Binds
import dagger.Module
import javax.inject.Inject
import javax.inject.Provider

@Module
abstract class BaseActivityModule {
    @Binds
    @ActivityScoped
    @ActivityScopedFactory
    abstract fun bindViewModelFactory(viewModelFactory: ActivityViewModelFactory): ViewModelFactory
}


@ActivityScoped
@Suppress("UNCHECKED_CAST")
class ActivityViewModelFactory @Inject constructor(
        viewModel: MutableMap<Class<out BaseActivityViewModel>, Provider<BaseActivityViewModel>>
) : ViewModelFactory(viewModel as Map<Class<out ViewModel>, Provider<ViewModel>>)