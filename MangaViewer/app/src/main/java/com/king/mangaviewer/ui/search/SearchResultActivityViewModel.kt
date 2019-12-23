package com.king.mangaviewer.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.repository.HistoryMangaRepository
import com.king.mangaviewer.domain.usecase.SearchMangaMenuUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchResultActivityViewModel @Inject constructor(
        private val AppRepository: AppRepository,
        private val MangaRepository: HistoryMangaRepository,
        private val selectMangaMenuUseCase: SelectMangaMenuUseCase,
        private val searchMangaMenuUseCase: SearchMangaMenuUseCase
) :
        BaseActivityViewModel() {

    private val hashState = HashMap<String, Any>()
    private val _mangaList = MutableLiveData<List<MangaMenuItem>>().apply { value = emptyList() }
    val mangaList: LiveData<List<MangaMenuItem>> = _mangaList

    fun selectMenu(menuItem: MangaMenuItem) {
        selectMangaMenuUseCase.execute(menuItem).subscribe()
    }

    fun searchManga(query: String) {
        //reset all manga list
        _mangaList.value = emptyList()
        hashState.clear()
        hashState[MangaProvider.STATE_SEARCH_QUERYTEXT] = query
        searchMangaMenuUseCase.execute(hashState)
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

    companion object {
        const val TAG = "SearchResultActivityViewModel"
    }
}
