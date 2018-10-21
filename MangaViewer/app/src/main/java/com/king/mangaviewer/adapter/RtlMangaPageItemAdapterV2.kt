package com.king.mangaviewer.adapter

import android.view.GestureDetector
import android.view.ViewGroup
import com.king.mangaviewer.component.PageView
import com.king.mangaviewer.component.ReadingDirection.RTL
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.Logger

class RtlMangaPageItemAdapterV2(data: List<MangaUri>, gestureDetector: GestureDetector)
    : MangaPageItemAdapterV2(data, gestureDetector) {

    override fun onBindView(view: PageView, position: Int) {
        val item = data[count - position - 1]
        Logger.d(TAG, "onBindView")
        view.readingDirection = RTL
        view.setData(item)
    }

    companion object {
        val TAG = "RtlMangaPageItemAdapterV2"
    }
}