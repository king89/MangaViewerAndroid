package com.king.mangaviewer.component

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior


class ChapterListBottomSheetBehavior<V : View> : BottomSheetBehavior<V> {

    private var allowDragging = true
    private var topView: View? = null

    constructor() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setAllowDragging(allowDragging: Boolean) {
        this.allowDragging = allowDragging
    }

    fun setTopView(view: View) {
        this.topView = view
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V,
        event: MotionEvent): Boolean {
        if (event.action == ACTION_DOWN) {
            val rect = Rect()
            topView?.getGlobalVisibleRect(rect)
            allowDragging = rect.contains(event.x.toInt(), event.y.toInt())
        }

        if (!allowDragging) {
            return false
        }

        return super.onInterceptTouchEvent(parent, child, event);

    }

    companion object {

        private val TAG = "[ChapterListBottomSheetBehavior]"
    }
}