package com.king.mangaviewer.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.GestureDetector
import com.king.mangaviewer.adapter.RtlMangaPageItemAdapterV2
import com.king.mangaviewer.component.OnOverScrollListener
import com.king.mangaviewer.component.ReaderListener
import com.king.mangaviewer.model.MangaUri
import kotlinx.android.synthetic.main.fragment_viewpager_reader.viewPager
import kotlin.math.max

class RtlViewPagerReaderFragment : ViewPagerReaderFragment() {

    override fun setPage(page: Int) {
        viewPager.setCurrentItem(page.invertIndex(), false)
    }

    override fun smoothScrollToPage(pageNum: Int) {
        viewPager.setCurrentItem(pageNum.invertIndex(), true)
    }

    override fun getCurrentPageNum(): Int {
        return super.getCurrentPageNum().invertIndex()
    }

    override fun getTotalPageNum(): Int {
        return mangaList?.size ?: 0
    }

    override fun showThumbnail(pageNum: Int): Bitmap? {
        return null
    }

    override fun tapLeft() {
        super.tapRight()
    }

    override fun tapRight() {
        super.tapLeft()
    }

    override fun setupAdapter(mangaList: List<MangaUri>, gestureDetector: GestureDetector) {
        viewPager.adapter = RtlMangaPageItemAdapterV2(mangaList, gestureDetector)
    }

    private fun Int.invertIndex(): Int {
        return max(getTotalPageNum() - this - 1, -1)
    }

    override fun createOnOverScrollListener(
            listener: ReaderListener): OnOverScrollListener {
        return object : OnOverScrollListener {
            override fun onOverScrollStarted(atRight: Boolean) {
            }

            override fun onOverScrollMove(atRight: Boolean, x: Float, y: Float) {
            }

            override fun onOverScrollFinished(atRight: Boolean) {
                if (!atRight) {
                    listener.nextChapter()
                } else {
                    listener.prevChapter()
                }
            }

        }
    }

    companion object {
        val TAG = "RtlViewPagerReaderFragment"
        @JvmStatic
        fun newInstance(dataJson: String) =
                RtlViewPagerReaderFragment().apply {
                    arguments = Bundle().apply {
                        putString(INTENT_EXTRA_MANGA_LIST_JSON, dataJson)
                    }
                }
    }
}