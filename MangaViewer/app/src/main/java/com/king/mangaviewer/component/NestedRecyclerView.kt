package com.king.mangaviewer.component

import android.content.Context
import android.support.v4.view.NestedScrollingParent2
import android.support.v4.view.NestedScrollingParentHelper
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SnapHelper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.animation.LinearInterpolator
import com.king.mangaviewer.util.Logger

class NestedRecyclerView : RecyclerView, NestedScrollingParent2 {

    val nestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private var nestedScrollTarget: View? = null
    private var nestedScrollTargetIsBeingDragged = false
    private var nestedScrollTargetWasUnableToScroll = false
    private var skipsTouchInterception = false

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        Logger.d("$TAG", "----父布局onNestedScrollAccepted----------------");
        if (axes and View.SCROLL_AXIS_VERTICAL != 0) {
            // A descendent started scrolling, so we'll observe it.
            nestedScrollTarget = target
            nestedScrollTargetIsBeingDragged = false
            nestedScrollTargetWasUnableToScroll = false
        }

        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes, type)
    }

    var firstScrollSignal: Int? = null
    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
//        if (firstScrollSignal == null) {
//            firstScrollSignal = dx
//        }
//        firstScrollSignal?.run {
//            if (firstScrollSignal!! * dx > 0) {
//                //Scroll
//                scrollBy(dx, 0)
//                smoothScrollBy(dx, 0)
//                consumed[0] = dx;
//                consumed[1] = 0; // 把消费的距离放进去
//                Logger.d("$TAG", "----父布局onNestedPreScroll----------------dx: $dx");
//            }
//        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        Logger.d(TAG, "----onStopNestedScroll----------------");
        // The descendent finished scrolling. Clean up!
        nestedScrollTarget = null
        nestedScrollTargetIsBeingDragged = false
        nestedScrollTargetWasUnableToScroll = false
        firstScrollSignal = null
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        Logger.d(TAG, "child==target:" + (child == target));

        Logger.d(TAG, "----父布局onStartNestedScroll----------------");

        return true
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
            dyUnconsumed: Int, type: Int) {
        Logger.d(TAG, "----onNestedScroll----------------: dxUnconsumed: $dxUnconsumed");
        if (target === nestedScrollTarget && !nestedScrollTargetIsBeingDragged) {
            if (dxConsumed != 0) {
                // The descendent was actually scrolled, so we won't bother it any longer.
                // It will receive all future events until it finished scrolling.
                nestedScrollTargetIsBeingDragged = true
                nestedScrollTargetWasUnableToScroll = false
            } else if (dxConsumed == 0 && dxUnconsumed != 0) {
                // The descendent tried scrolling in response to touch movements but was not able to do so.
                // We remember that in order to allow RecyclerView to take over scrolling.
                nestedScrollTargetWasUnableToScroll = true
                target.parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
    }

    override fun getNestedScrollAxes(): Int {
        Logger.d(TAG, "----父布局getNestedScrollAxes----------------");
        return nestedScrollingParentHelper.nestedScrollAxes
    }

    val snapHelper = LinearSnapHelper()

    constructor(context: Context) :
            super(context)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        snapHelper.attachToRecyclerView(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val temporarilySkipsInterception = nestedScrollTarget != null
        if (temporarilySkipsInterception) {
            // If a descendent view is scrolling we set a flag to temporarily skip our onInterceptTouchEvent implementation
            skipsTouchInterception = true
        }

        // First dispatch, potentially skipping our onInterceptTouchEvent
        var handled = super.dispatchTouchEvent(ev)

        if (temporarilySkipsInterception) {
            skipsTouchInterception = false

            // If the first dispatch yielded no result or we noticed that the descendent view is unable to scroll in the
            // direction the user is scrolling, we dispatch once more but without skipping our onInterceptTouchEvent.
            // Note that RecyclerView automatically cancels active touches of all its descendents once it starts scrolling
            // so we don't have to do that.
            if (!handled || nestedScrollTargetWasUnableToScroll) {
                handled = super.dispatchTouchEvent(ev)
            }
        }

        return handled
    }

    override fun onInterceptTouchEvent(e: MotionEvent) =
            !skipsTouchInterception && super.onInterceptTouchEvent(e)

//    // Skips RecyclerView's onInterceptTouchEvent if requested
//    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
//        Logger.d("$TAG", "NestedRecyclerView onInterceptTouchEvent $e")
//
////        val canScroll = gestureDetector.onTouchEvent(e)
//        return !canScroll
//    }

//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        val handled = gestureDetector.onTouchEvent(event)
//        if (!handled && event.action == MotionEvent.ACTION_UP) {
//            stopNestedScroll()
//        }
//        return true
//    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        Logger.d("$TAG", "onTouchEvent")
        val result = super.onTouchEvent(e)
        if (e?.action == ACTION_UP) {
            //snap to position
            val view = snapHelper.findSnapView(layoutManager)
            val array = snapHelper.calculateDistanceToFinalSnap(layoutManager, view!!)!!
            Logger.d("$TAG", "snapHelper ${array[0]},${array[1]}")

            smoothScrollBy(array[0], array[0])
        }
        return result
    }

    //
//
    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange()
        }
    }

    private fun notifyPageChanged() {
        Logger.d("$TAG", "notifyPageChanged, Page:$snapPosition")

        val view = findViewHolderForAdapterPosition(snapPosition)?.itemView
        view?.run {
            if (this is CustomScrollView) {
                this.resetScrollState()
            }
        }
    }

    private var snapPosition: Int = NO_POSITION

    private fun maybeNotifySnapPositionChange() {
        val snapPosition = snapHelper.getSnapPosition(this)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            //Do something
            this.snapPosition = snapPosition
            notifyPageChanged()
        }
    }

    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }

    companion object {
        val TAG = "NestedRecyclerView"
    }
}