package com.king.mangaviewer.component

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.king.mangaviewer.R
import com.king.mangaviewer.R.integer
import com.king.mangaviewer.adapter.MangaMenuItemAdapter

class MangaGridView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0) : androidx.recyclerview.widget.RecyclerView(context, attrs,
        defStyleAttr) {

    private var flagLoading: Boolean = false
    private val flagLock = Any()
    private var mLoadingFooter: View? = null
    private var mNoMore = false
    private var mGridLayoutManager: androidx.recyclerview.widget.GridLayoutManager? = null

    private var mOnScrollListener: OnScrollListener = object :
        OnScrollListener() {

        override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int,
            dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val VISIBLE_THRESHOLD = 3
            val firstVisibleItem = mGridLayoutManager!!.findFirstVisibleItemPosition()
            val visibleItemCount = recyclerView!!.childCount
            val totalItemCount = recyclerView.adapter?.itemCount ?: 0
            if (firstVisibleItem + visibleItemCount >= totalItemCount - VISIBLE_THRESHOLD && totalItemCount != 0) {
                if (!getFlagLoading() && !mNoMore) {
                    (adapter as? MangaMenuItemAdapter)?.run {

                    }
                }
            }
        }
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        mGridLayoutManager!!.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return (adapter as? MangaMenuItemAdapter)?.getSpanSize(position,
                        resources.getInteger(R.integer.gridvivew_column_num)) ?: 1
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val layoutManager = this.layoutManager as androidx.recyclerview.widget.GridLayoutManager
        layoutManager.spanCount = resources.getInteger(R.integer.gridvivew_column_num)
    }

    init {
        this.addOnScrollListener(mOnScrollListener)
        mLoadingFooter = TextView(context)
        mGridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(context,
            resources.getInteger(integer.gridvivew_column_num))
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        this.addItemDecoration(SpacesItemDecoration(spacingInPixels))
        this@MangaGridView.layoutManager = mGridLayoutManager
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

    inner class SpacesItemDecoration(
        private val space: Int) : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View,
            parent: androidx.recyclerview.widget.RecyclerView, state: State) {
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
