package com.king.mangaviewer.component

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.AsyncTask
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.TextView

import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.MangaViewModel

import java.util.ArrayList
import java.util.HashMap

class MangaGridView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : RecyclerView(context, attrs,
        defStyleAttr) {

    private val mStateHash: HashMap<String, Any>? = null
    private var flagLoading: Boolean = false
    private val flagLock = Any()
    private val mMangaList: MutableList<MangaMenuItem>? = null
    private var mLoadingFooter: View? = null
    private var mNoMore = false
    private var mGridLayoutManager: GridLayoutManager? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null

    private var mOnScrollListener: RecyclerView.OnScrollListener = object :
            RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val VISIBLE_THRESHOLD = 3
            val firstVisibleItem = mGridLayoutManager!!.findFirstVisibleItemPosition()
            val visibleItemCount = recyclerView!!.childCount
            val totalItemCount = recyclerView.adapter.itemCount
            if (firstVisibleItem + visibleItemCount >= totalItemCount - VISIBLE_THRESHOLD && totalItemCount != 0) {
                if (getFlagLoading() == false && !mNoMore) {
                    getMoreManga()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val layoutManager = this.layoutManager as GridLayoutManager
        layoutManager.spanCount = resources.getInteger(R.integer.gridvivew_column_num)
    }

    init {
        this.addOnScrollListener(mOnScrollListener)
        mLoadingFooter = TextView(context)
        mGridLayoutManager = GridLayoutManager(context,
                resources.getInteger(R.integer.gridvivew_column_num))
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        this.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        this@MangaGridView.layoutManager = mGridLayoutManager
    }

    fun setLoadingFooter(view: View) {
        mLoadingFooter = view
    }

    fun setSwipeRefreshLayout(layout: SwipeRefreshLayout) {
        mSwipeRefreshLayout = layout
    }

    protected fun getMoreManga() {
        setFlagLoading(true)
        mLoadingFooter!!.visibility = View.VISIBLE
    }

    fun setFlagLoading(flagLoading: Boolean) {
        synchronized(flagLock) {
            this.flagLoading = flagLoading
        }
    }

    fun getFlagLoading(): Boolean {
        synchronized(flagLock) {
            return flagLoading
        }
    }

    inner class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View,
                parent: RecyclerView, state: RecyclerView.State?) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
            outRect.top = space

        }
    }

    companion object {
        const val TAG = "MangaGridView"
    }

}
