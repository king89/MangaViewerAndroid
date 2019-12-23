package com.king.mangaviewer.adapter

import android.view.View
import android.view.ViewGroup
import com.king.mangaviewer.util.Logger
import java.util.Stack

abstract class RecyclerPagerAdapter<T : View> : androidx.viewpager.widget.PagerAdapter() {

    private val mViewPool: Stack<T> = Stack()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = if (mViewPool.isEmpty()) onCreateView(container) else mViewPool.pop()
        onBindView(view, position)
        container.addView(view)
        Logger.d("RecyclerPagerAdapter", "view pool size : ${mViewPool.size}")
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
        onRecyclerView(`object` as T)
        mViewPool.push(`object`)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    protected abstract fun onCreateView(container: ViewGroup): T

    protected abstract fun onBindView(view: T, position: Int)

    protected abstract fun onRecyclerView(view: T)
}