package com.king.mangaviewer.ui.main.local

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.usecase.AddLocalMangaMenuUseCase
import com.king.mangaviewer.domain.usecase.GetLatestMangaListUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.util.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class LocalFragmentViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val getLatestMangaListUseCase: GetLatestMangaListUseCase,
    private val selectMangaMenuUseCase: SelectMangaMenuUseCase,
    private val addLocalMangaMenuUseCase: AddLocalMangaMenuUseCase
) : BaseFragmentViewModel() {

    private val _mangaList = MutableLiveData<List<MangaMenuItem>>()
    val mangaList: LiveData<List<MangaMenuItem>> = _mangaList

    override fun attachToView() {
    }

    private fun getData(isSilent: Boolean) {
        getLatestMangaListUseCase.execute(MangaWebSource.LOCAL)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                if (!isSilent) mLoadingState.value = Loading
            }
            .doAfterTerminate { mLoadingState.value = Idle }
            .subscribe({
                _mangaList.postValue(it)
            }, {
                Logger.e(TAG, it)
            })
            .apply { disposable.add(this) }
    }

    //    fun selectChapter(path: File, fileName: String) {
//
//        val tLocalManga = LocalMangaProvider(localMangaRepository)
//        val tmenu = MangaMenuItem("${path.absolutePath}", "${path.name}", "", "", "${path.absolutePath}",
//            MangaWebSource.LOCAL)
//
//        val chapterList = tLocalManga.getChapterList(tmenu)
//        var tSelectedChapter: MangaChapterItem? = null
//
//        val list = ArrayList<MangaChapterItem>()
//        for (i in chapterList!!.indices) {
//            val item = MangaChapterItem("Chapter-$i", chapterList[i]
//                .title, "", chapterList[i].imagePath,
//                chapterList[i].url, tmenu)
//            list.add(item)
//            if (item.title.equals(fileName, ignoreCase = true)) {
//                tSelectedChapter = item
//            }
//        }
//        selectMangaMenuUseCase.execute(tmenu).subscribe()
//        appRepository.appViewModel.Manga.mangaChapterList = list
//        selectMangaChapterUseCase.execute(tSelectedChapter!!).subscribe()
//    }
    fun selectMangaMenu(menuItem: MangaMenuItem) {
        selectMangaMenuUseCase.execute(menuItem).subscribe()
    }

    fun refresh(isSilent: Boolean) {
        getData(isSilent)
    }

    fun addLocalMenu(file: File, onSuccess: () -> Unit) {
        //TODO remove schedulers
        addLocalMangaMenuUseCase.execute(file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete { onSuccess.invoke() }
            .subscribe()
    }

    companion object {
        const val TAG = "LocalFragmentViewModel"
    }
}
