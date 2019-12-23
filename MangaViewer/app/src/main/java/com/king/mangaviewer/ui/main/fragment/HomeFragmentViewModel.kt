package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.base.ErrorMessage.GenericError
import com.king.mangaviewer.base.ErrorMessage.NoError
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.usecase.GetLatestMangaListUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomeFragmentViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val getLatestMangaListUseCase: GetLatestMangaListUseCase,
    private val selectMangaMenuUseCase: SelectMangaMenuUseCase
) : BaseFragmentViewModel() {

    private val _mangaList = MutableLiveData<List<MangaMenuItem>>()
    val mangaList: LiveData<List<MangaMenuItem>> = _mangaList

    override fun attachToView() {
        getData()
    }

    fun getData() {
        getLatestMangaListUseCase.execute(appRepository.appViewModel.Setting.selectedWebSource)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mLoadingState.value = Loading }
            .doAfterTerminate { mLoadingState.value = Idle }
            .subscribe({
                _mangaList.value = it
                mErrorMessage.value = NoError
            }, {
                Logger.e(TAG, it)
                mErrorMessage.value = GenericError
            })
            .apply { disposable.add(this) }
    }

    fun selectMangaMenu(menuItem: MangaMenuItem) {
        selectMangaMenuUseCase.execute(menuItem).subscribe()
    }

    companion object {
        const val TAG = "HomeFragmentViewModel"
    }
}
