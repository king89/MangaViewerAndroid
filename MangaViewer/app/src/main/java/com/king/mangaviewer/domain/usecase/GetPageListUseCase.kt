package com.king.mangaviewer.domain.usecase

import android.content.Context
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaPageItem
import io.reactivex.Single

class GetPageListUseCase(context: Context, chapter: MangaChapterItem) {
    fun execute(): Single<List<MangaPageItem>> {

        return Single.error(Exception())
    }
}