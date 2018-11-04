package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.usecase.GetLatestMangaListUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import io.reactivex.Single
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

    fun getData() {
        getLatestMangaListUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { mLoadingState.value = Loading }
                .doAfterTerminate { mLoadingState.value = Idle }
                .subscribe({
                    _mangaList.value = it
                }, {
                    Logger.e(TAG, it)
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
