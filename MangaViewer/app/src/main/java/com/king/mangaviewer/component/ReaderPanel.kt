package com.king.mangaviewer.component

import android.graphics.Bitmap
import android.widget.SeekBar

interface ReaderPanel {
    fun nextPage()
    fun prevPage()
    fun setPage(page: Int)
    fun smoothScrollToPage(pageNum: Int)
    fun showThumbnail(pageNum: Int): Bitmap?
    fun getCurrentPageNum(): Int
    fun getTotalPageNum(): Int
    fun setPageMode(mode: Int)
}

interface ReaderListener {

    fun onPageChanged(currentPage: Int)
}

interface HasFullScreenControl {
    abstract var isFullScreen: Boolean

    fun toggleUI()
    fun hideSystemUI()
    fun showSystemUI()
}