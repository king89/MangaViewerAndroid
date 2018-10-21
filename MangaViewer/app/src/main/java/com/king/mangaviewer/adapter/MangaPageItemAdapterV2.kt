package com.king.mangaviewer.adapter

import android.view.GestureDetector
import android.view.ViewGroup
import com.king.mangaviewer.component.PageView
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.Logger

open class MangaPageItemAdapterV2(val data: List<MangaUri>, val gestureDetector: GestureDetector) :
        RecyclerPagerAdapter<PageView>() {
    override fun onCreateView(container: ViewGroup): PageView {
        val pageView = PageView(container.context)
        pageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        pageView.setTapDetector(gestureDetector)
        return pageView
    }

    override fun onBindView(view: PageView, position: Int) {
        val item = data[position]
        Logger.d(TAG, "onBindView")
        view.setData(item)
    }

    override fun onRecyclerView(view: PageView) {
        view.recycle()
    }

    override fun getCount(): Int {
        return data.size
    }

    companion object {
        val TAG = "MangaPageItemAdapterV2"
    }
}