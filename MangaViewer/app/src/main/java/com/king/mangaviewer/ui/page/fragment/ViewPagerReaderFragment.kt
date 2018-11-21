package com.king.mangaviewer.ui.page.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.support.v4.view.ViewPager.VISIBLE
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import com.google.gson.reflect.TypeToken
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaPageItemAdapterV2
import com.king.mangaviewer.component.ReaderCallback
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment.ShouldChangeChapter.Idle
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment.ShouldChangeChapter.NextChapter
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment.ShouldChangeChapter.PrevChapter
import com.king.mangaviewer.util.GsonHelper
import com.king.mangaviewer.util.Logger
import kotlinx.android.synthetic.main.fragment_viewpager_reader.clMask
import kotlinx.android.synthetic.main.fragment_viewpager_reader.groupLeft
import kotlinx.android.synthetic.main.fragment_viewpager_reader.groupRight
import kotlinx.android.synthetic.main.fragment_viewpager_reader.tvLeft
import kotlinx.android.synthetic.main.fragment_viewpager_reader.tvRight
import kotlinx.android.synthetic.main.fragment_viewpager_reader.tvToChapter
import kotlinx.android.synthetic.main.fragment_viewpager_reader.tvToChapterTitle
import kotlinx.android.synthetic.main.fragment_viewpager_reader.viewPager
import me.everything.android.ui.overscroll.IOverScrollState.STATE_BOUNCE_BACK
import me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_END_SIDE
import me.everything.android.ui.overscroll.IOverScrollState.STATE_DRAG_START_SIDE
import me.everything.android.ui.overscroll.IOverScrollState.STATE_IDLE
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import kotlin.math.abs
import kotlin.math.min

open class ViewPagerReaderFragment : ReaderFragment() {

    private var shouldChangeChapter: ShouldChangeChapter = Idle
    protected open val isLeftToRight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val type = object : TypeToken<List<MangaUri>>() {}.type
            mangaList = GsonHelper.fromJson(it.getString(
                    INTENT_EXTRA_MANGA_LIST_JSON), type)
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

        (activity as? ReaderCallback)?.run {
            createOnOverScrollListener(this)
        }

        mangaList?.run {
            setupAdapter(this, GestureDetector(context, TapDetector()))
            setPage(startPage)
        }

    }

    sealed class ShouldChangeChapter {
        object Idle : ShouldChangeChapter()
        object PrevChapter : ShouldChangeChapter()
        object NextChapter : ShouldChangeChapter()
    }

    protected open fun createOnOverScrollListener(
            callback: ReaderCallback) {
        val decro = OverScrollDecoratorHelper.setUpOverScroll(viewPager)

        val tvStart = if (isLeftToRight) tvLeft else tvRight
        val tvEnd = if (isLeftToRight) tvRight else tvLeft
        val toChapterStringStart = if (isLeftToRight) getString(
                R.string.prev_chapter) else getString(R.string.next_chapter)
        val toChapterStringEnd = if (isLeftToRight) getString(R.string.next_chapter) else getString(
                R.string.prev_chapter)
        val leftChapterName = if (isLeftToRight) "Prev chapter title" else "Next chapter title"
        val rightChapterName = if (isLeftToRight) "Next chapter title" else "Prev chapter title"

        decro.setOverScrollStateListener { decor, oldState, newState ->
            Logger.d(TAG, "oldState:$oldState, newState:$newState")
            when (newState) {
                STATE_IDLE -> {
                    clMask.visibility = GONE
                    groupLeft.visibility = GONE
                    groupRight.visibility = GONE
                    when (shouldChangeChapter) {
                        PrevChapter -> callback.prevChapter()
                        NextChapter -> callback.nextChapter()
                    }
                    shouldChangeChapter = Idle
                }
                STATE_DRAG_START_SIDE -> {
                    shouldChangeChapter = Idle
                    clMask.visibility = VISIBLE
                    groupLeft.visibility = VISIBLE
                    groupRight.visibility = GONE
                    tvStart.text = getString(R.string.pull_to_prev_chapter)
                    tvEnd.text = getString(R.string.pull_to_next_chapter)
                    tvToChapter.text = toChapterStringStart
                    tvToChapterTitle.text = leftChapterName
                }
                STATE_DRAG_END_SIDE -> {
                    shouldChangeChapter = Idle
                    clMask.visibility = VISIBLE
                    groupLeft.visibility = GONE
                    groupRight.visibility = VISIBLE
                    tvStart.text = getString(R.string.pull_to_prev_chapter)
                    tvEnd.text = getString(R.string.pull_to_next_chapter)
                    tvToChapter.text = toChapterStringEnd
                    tvToChapterTitle.text = rightChapterName

                }
                STATE_BOUNCE_BACK -> {
                    if (abs(decor.view.translationX / decor.view.width) > THRESHOLD_SCROLL) {
                        shouldChangeChapter = when (oldState) {
                            STATE_DRAG_START_SIDE -> if (isLeftToRight) PrevChapter else NextChapter
                            STATE_DRAG_END_SIDE -> if (isLeftToRight) NextChapter else PrevChapter
                            else -> Idle
                        }
                    }
                }

            }
        }
        decro.setOverScrollUpdateListener { decor, state, offset ->
            val alpha = min(abs(offset) / decor.view.width, THRESHOLD_SCROLL) / THRESHOLD_SCROLL
            Logger.d(TAG,
                    "OverScrollUpdate alpha: $alpha, percent: ${abs(offset) / decor.view.width}")
            clMask.alpha = alpha
            when {
                //change text to release
                alpha >= 1 && state == STATE_DRAG_START_SIDE -> {
                    tvStart.text = getString(R.string.release_to_prev_chapter)
                }
                alpha >= 1 && state == STATE_DRAG_END_SIDE -> {
                    tvEnd.text = getString(R.string.release_to_next_chapter)
                }
                alpha < 1 && state == STATE_DRAG_START_SIDE -> {
                    tvStart.text = getString(R.string.pull_to_prev_chapter)
                }
                alpha < 1 && state == STATE_DRAG_END_SIDE -> {
                    tvEnd.text = getString(R.string.pull_to_next_chapter)
                }
            }
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
        val THRESHOLD_SCROLL = 0.18f

        @JvmStatic
        fun newInstance(dataJson: String) =
                ViewPagerReaderFragment().apply {
                    arguments = Bundle().apply {
                        putString(
                                INTENT_EXTRA_MANGA_LIST_JSON, dataJson)
                    }
                }
    }
}