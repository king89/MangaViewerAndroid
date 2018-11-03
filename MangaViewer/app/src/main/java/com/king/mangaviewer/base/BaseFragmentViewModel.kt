package com.king.mangaviewer.base

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.king.mangaviewer.model.LoadingState
import com.king.mangaviewer.model.LoadingState.Idle
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragmentViewModel : ViewModel() {
    val disposable: CompositeDisposable = CompositeDisposable()
    protected val mLoadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = mLoadingState
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    init {
        mLoadingState.value = Idle
    }
}