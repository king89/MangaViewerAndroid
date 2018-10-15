package com.king.mangaviewer.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.mangaviewer.R
import com.king.mangaviewer.component.HasFullScreenControl
import com.king.mangaviewer.component.ReaderListener
import com.king.mangaviewer.component.ReaderPanel

abstract class ReaderFragment : BaseFragment(), ReaderPanel {

    var readerListener: ReaderListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.list_manga_page_item_v2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    protected fun toggleUI() {
        (activity as? HasFullScreenControl)?.apply {
            toggleUI()
        }
    }

    companion object {
        val TAG = "ReaderFragment"
    }
}