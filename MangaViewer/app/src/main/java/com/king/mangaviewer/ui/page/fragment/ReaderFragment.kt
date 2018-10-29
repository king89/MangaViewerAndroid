package com.king.mangaviewer.ui.page.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.mangaviewer.R
import com.king.mangaviewer.activity.BaseFragment
import com.king.mangaviewer.component.HasFullScreenControl
import com.king.mangaviewer.component.ReaderListener
import com.king.mangaviewer.component.ReaderPanel
import com.king.mangaviewer.model.MangaUri

abstract class ReaderFragment : BaseFragment(), ReaderPanel {

    var readerListener: ReaderListener? = null
    protected var mangaList: List<MangaUri>? = null
    var startPage = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.list_manga_page_item_v2, container, false)
    }

    protected fun toggleUI() {
        (activity as? HasFullScreenControl)?.apply {
            toggleUI()
        }
    }

    override fun nextPage() {
        val newPos = getCurrentPageNum() + 1
        if (newPos in 0 until getTotalPageNum()){
            smoothScrollToPage(newPos)
        }
    }

    override fun prevPage() {
        val newPos = getCurrentPageNum() - 1
        if (newPos in 0 until getTotalPageNum()){
            smoothScrollToPage(newPos)
        }
    }

    abstract fun tapLeft()

    abstract fun tapRight()

    companion object {
        const val INTENT_EXTRA_MANGA_LIST_JSON = "param1"
        val TAG = "ReaderFragment"
    }
}