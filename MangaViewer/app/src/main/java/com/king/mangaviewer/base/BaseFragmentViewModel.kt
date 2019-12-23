package com.king.mangaviewer.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.king.mangaviewer.base.ErrorMessage.NoError
import com.king.mangaviewer.model.LoadingState
import com.king.mangaviewer.model.LoadingState.Idle
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragmentViewModel : ViewModel() {
    val disposable: CompositeDisposable = CompositeDisposable()
    protected val mLoadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = mLoadingState

    protected val mErrorMessage = MutableLiveData<ErrorMessage>().apply { value = NoError }
    val errorMessage: LiveData<ErrorMessage> = mErrorMessage

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    abstract fun attachToView()

    init {
        mLoadingState.value = Idle
    }
}

sealed class ErrorMessage {
    object NoError : ErrorMessage()
    open class ViewModelError : ErrorMessage()
    object GenericError : ErrorMessage()
}
