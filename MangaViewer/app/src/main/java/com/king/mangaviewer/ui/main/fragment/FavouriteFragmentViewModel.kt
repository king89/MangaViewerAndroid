package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.usecase.GetFavoriteMangaListUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FavouriteFragmentViewModel @Inject constructor(
        private val appRepository: AppRepository,
        private val getFavoriteMangaListUseCase: GetFavoriteMangaListUseCase,
        private val selectMangaMenuUseCase: SelectMangaMenuUseCase
) : BaseFragmentViewModel() {

    private val _mangaList = MutableLiveData<List<FavouriteMangaMenuItem>>()
    val mangaList: LiveData<List<FavouriteMangaMenuItem>> = _mangaList

    override fun attachToView() {
        getData(false)
    }

    private fun getData(isSilent: Boolean) {
        getFavoriteMangaListUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    if (!isSilent) mLoadingState.value = Loading
                }
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

    fun refresh(isSilent: Boolean) {
        getData(isSilent)
    }

    companion object {
        const val TAG = "FavouriteFragmentViewModel"
    }
}
