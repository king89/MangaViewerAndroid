package com.king.mangaviewer.component

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

internal class OverScrollPager @JvmOverloads constructor(context: Context,
        attrs: AttributeSet? = null) :
        ViewPager(context, attrs) {

    private var mStartX: Float = 0.toFloat()

    private var mOverScrollListener: OnOverScrollListener? = null

    val itemCount: Int
        get() {
            val adapter = adapter
            return adapter?.count ?: 0
        }

    fun setOnOverScrollListener(listener: OnOverScrollListener) {
        mOverScrollListener = listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            if (ev.actionMasked == MotionEvent.ACTION_DOWN && ev.pointerCount == 1) {
                val currentItem = currentItem
                if (currentItem == 0 || currentItem == itemCount - 1) {
                    mStartX = ev.x
                }
            }

            return super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            return false
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mOverScrollListener == null || ev.pointerCount != 1) {
            return super.onTouchEvent(ev)
        }
        val currentItem = currentItem
        try {
            if (currentItem == 0) {
                if (ev.actionMasked == MotionEvent.ACTION_UP) {
                    val displacement = ev.x - mStartX

                    if (ev.x > mStartX && displacement > measuredWidth * SWIPE_TOLERANCE) {
                       mOverScrollListener?.onOverScrollEnd(true)
                        return true
                    }
                    mStartX = 0f
                }
            } else if (currentItem == itemCount - 1) {
                if (ev.actionMasked == MotionEvent.ACTION_UP) {
                    val displacement = mStartX - ev.x

                    if (ev.x < mStartX && displacement > measuredWidth * SWIPE_TOLERANCE) {
                        mOverScrollListener?.onOverScrollEnd(false)
                        return true
                    }
                    mStartX = 0f
                }
            }

            return super.onTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            return false
        }

    }

    companion object {

        private val SWIPE_TOLERANCE = .25f
    }
}

interface OnOverScrollListener {
    fun onOverScrollStart(isToRight: Boolean)
    fun onOverScrollMove(isToRight: Boolean, x: Float, y: Float)
    fun onOverScrollEnd(isToRight: Boolean)
}
