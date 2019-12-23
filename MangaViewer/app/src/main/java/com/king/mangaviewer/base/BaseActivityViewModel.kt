package com.king.mangaviewer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.king.mangaviewer.model.LoadingState
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivityViewModel : ViewModel() {
    val disposable: CompositeDisposable = CompositeDisposable()
    protected val mLoadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = mLoadingState
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    //should call as a init method
    open fun attachToView() {}
}