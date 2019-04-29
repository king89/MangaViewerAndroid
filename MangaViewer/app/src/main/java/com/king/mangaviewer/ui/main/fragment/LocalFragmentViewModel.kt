package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.king.mangaviewer.base.BaseFragmentViewModel
import com.king.mangaviewer.domain.repository.AppRepository
import com.king.mangaviewer.domain.external.mangaprovider.LocalMangaProvider
import com.king.mangaviewer.domain.usecase.SelectMangaChapterUseCase
import com.king.mangaviewer.domain.usecase.SelectMangaMenuUseCase
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaWebSource
import java.io.File
import java.util.ArrayList
import javax.inject.Inject

class LocalFragmentViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val selectMangaMenuUseCase: SelectMangaMenuUseCase,
    private val selectMangaChapterUseCase: SelectMangaChapterUseCase
) : BaseFragmentViewModel() {

    private val _mangaList = MutableLiveData<List<MangaMenuItem>>()
    val mangaList: LiveData<List<MangaMenuItem>> = _mangaList

    override fun attachToView() {
    }

    fun selectChapter(path: File, fileName: String) {

        val tLocalManga = LocalMangaProvider()
        val tmenu = MangaMenuItem("${path.absolutePath}", "${path.name}", "", "", "${path.absolutePath}",
            MangaWebSource.LOCAL)

        val chapterList = tLocalManga.getChapterList(tmenu)
        var tSelectedChapter: MangaChapterItem? = null

        val list = ArrayList<MangaChapterItem>()
        for (i in chapterList!!.indices) {
            val item = MangaChapterItem("Chapter-$i", chapterList[i]
                .title, "", chapterList[i].imagePath,
                chapterList[i].url, tmenu)
            list.add(item)
            if (item.title.equals(fileName, ignoreCase = true)) {
                tSelectedChapter = item
            }
        }
        selectMangaMenuUseCase.execute(tmenu).subscribe()
        appRepository.appViewModel.Manga.mangaChapterList = list
        selectMangaChapterUseCase.execute(tSelectedChapter!!).subscribe()
    }

    companion object {
        const val TAG = "LocalFragmentViewModel"
    }
}
