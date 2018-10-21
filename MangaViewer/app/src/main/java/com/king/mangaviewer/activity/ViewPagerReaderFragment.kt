package com.king.mangaviewer.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.gson.reflect.TypeToken
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaPageItemAdapterV2
import com.king.mangaviewer.component.OnOverScrollListener
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.GsonHelper
import com.king.mangaviewer.util.Logger
import kotlinx.android.synthetic.main.fragment_viewpager_reader.viewPager

open class ViewPagerReaderFragment : ReaderFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val type = object : TypeToken<List<MangaUri>>() {}.type
            mangaList = GsonHelper.fromJson(it.getString(INTENT_EXTRA_MANGA_LIST_JSON), type)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_viewpager_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                    positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                val pageNum = position
                readerListener?.onPageChanged(pageNum)
            }
        })

        (activity as? OnOverScrollListener)?.run {
            viewPager.setOnOverScrollListener(this)
        }

        mangaList?.run {
            setupAdapter(this, GestureDetector(context, TapDetector()))
            setPage(startPage)
        }

    }

    protected open fun setupAdapter(mangaList: List<MangaUri>, gestureDetector: GestureDetector) {
        viewPager.adapter = MangaPageItemAdapterV2(mangaList, gestureDetector)
    }

    private inner class TapDetector : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Logger.d(TAG, "onSingleTapConfirmed")
            toggleUI()
            return true
        }
    }

    override fun setPage(page: Int) {
        viewPager.setCurrentItem(page, false)
    }

    override fun smoothScrollToPage(pageNum: Int) {
        viewPager.setCurrentItem(pageNum, true)
    }

    override fun getCurrentPageNum(): Int {
        return viewPager?.currentItem ?: 0
    }

    override fun getTotalPageNum(): Int {
        return mangaList?.size ?: 0
    }

    override fun setPageMode(mode: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showThumbnail(pageNum: Int): Bitmap? {
        return null
    }

    override fun tapLeft() {
    }

    override fun tapRight() {
    }

    companion object {
        val TAG = "ViewPagerReaderFragment"
        @JvmStatic
        fun newInstance(dataJson: String) =
                ViewPagerReaderFragment().apply {
                    arguments = Bundle().apply {
                        putString(INTENT_EXTRA_MANGA_LIST_JSON, dataJson)
                    }
                }
    }
}