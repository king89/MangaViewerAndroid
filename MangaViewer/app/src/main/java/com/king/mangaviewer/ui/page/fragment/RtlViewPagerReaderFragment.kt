package com.king.mangaviewer.ui.page.fragment

import android.graphics.Bitmap
import android.view.GestureDetector
import androidx.viewpager.widget.ViewPager
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.RtlMangaPageItemAdapterV2
import com.king.mangaviewer.model.MangaUri
import kotlin.math.max

class RtlViewPagerReaderFragment : ViewPagerReaderFragment() {

    override val isLeftToRight: Boolean = false
    private val viewPager: ViewPager? by lazy { this.view?.findViewById(R.id.viewPager) }
    override fun setPage(page: Int) {
        viewPager?.setCurrentItem(page.invertIndex(), false)
    }

    override fun smoothScrollToPage(pageNum: Int) {
        viewPager?.setCurrentItem(pageNum.invertIndex(), true)
    }

    override fun getCurrentPageNum(): Int {
        return super.getCurrentPageNum().invertIndex()
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
        viewPager?.adapter = RtlMangaPageItemAdapterV2(mangaList, gestureDetector)
    }

    private fun Int.invertIndex(): Int {
        return max(getTotalPageNum() - this - 1, -1)
    }


    companion object {
        val TAG = "RtlViewPagerReaderFragment"

        @JvmStatic
        fun newInstance() =
            RtlViewPagerReaderFragment()
    }
}