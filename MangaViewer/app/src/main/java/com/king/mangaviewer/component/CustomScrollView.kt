package com.king.mangaviewer.component

import android.content.Context
import android.support.animation.DynamicAnimation
import android.support.animation.FlingAnimation
import android.support.v4.view.NestedScrollingChild2
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat.TYPE_TOUCH
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import com.king.mangaviewer.util.Logger

class CustomScrollView @JvmOverloads constructor(context: Context,
        attributeSet: AttributeSet? = null, defStyleAttr: Int = 0) :
        HorizontalScrollView(context, attributeSet, defStyleAttr), NestedScrollingChild2 {
    val nestedScrollingChildHelper = NestedScrollingChildHelper(this)

    init {
        setNestedScrollingEnabled(true);
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        nestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return nestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
            dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
        Logger.d(TAG, "-----------子View把剩余的滚动距离传给父布局---------------");

        return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed, offsetInWindow, type)

    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        Logger.d(TAG, "-----------子View开始滚动---------------");

        return nestedScrollingChildHelper.startNestedScroll(axes, type)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?,
            offsetInWindow: IntArray?, type: Int): Boolean {
        Logger.d(TAG, "-----------子View把总的滚动距离传给父布局--------------- dx: $dx");

        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow,
                type)
    }

    override fun stopNestedScroll(type: Int) {
        Logger.d(TAG, "-----------子View停止滚动---------------")

        return nestedScrollingChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return nestedScrollingChildHelper.hasNestedScrollingParent(type)
    }

    val gestureDetector: GestureDetector
    var canScroll = true
    val scrollDetecter = object : SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float,
                distanceY: Float): Boolean {
            Logger.d("-=-=", "CustomScrollView x: ${this@CustomScrollView.x}")

            Logger.d("-=-=", "CustomScrollView onScroll: distanceX:$distanceX, e1:$e1, e2:$e2")

            if (canScroll && canScroll(distanceX)) {
                scrollBy(distanceX.toInt(), distanceY.toInt())
                this@CustomScrollView.dispatchNestedScroll(distanceX.toInt(), 0, 0, 0, null,
                        TYPE_TOUCH)

            } else {
                canScroll = false
//                this@CustomScrollView.dispatchNestedPreScroll(distanceX.toInt(), distanceY.toInt(),
//                        null, null,
//                        TYPE_TOUCH)
                this@CustomScrollView.dispatchNestedScroll(0, 0, distanceX.toInt(), 0, null,
                        TYPE_TOUCH)
            }
            return true
        }

        var flingAnimation: FlingAnimation? = null
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
                velocityY: Float): Boolean {
            val childHeight = getChildAt(0)?.width ?: 0

            val maxScroll = -width + (childHeight + paddingLeft + paddingRight)
            flingAnimation?.cancel()
            flingAnimation = FlingAnimation(this@CustomScrollView, DynamicAnimation.SCROLL_X).apply {
                setStartVelocity(-velocityX)
                setMinValue(0f)
                setMaxValue(maxScroll.toFloat())
                friction = 1.1f
                start()
            }
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            Logger.d("-=-=", "CustomScrollView onDown")
            flingAnimation?.cancel()

            canScroll = true
            this@CustomScrollView.startNestedScroll(SCROLL_AXIS_VERTICAL, TYPE_TOUCH)
            return true
        }

    }

    fun resetScrollState() {
        canScroll = true
    }

    fun canScroll(distanceX: Float): Boolean {
        val childHeight = getChildAt(0)?.width ?: 0
        canScroll = if (distanceX > 0) {
            width + scrollX < childHeight + paddingLeft + paddingRight
        } else {
            scrollX > 0
        }
        return canScroll
    }

    init {
        gestureDetector = GestureDetector(context, scrollDetecter)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Logger.d("-=-=", "CustomScrollView onTouchEvent")

        val handled = gestureDetector.onTouchEvent(event)
        if (!handled && event.action == MotionEvent.ACTION_UP) {
            stopNestedScroll(TYPE_TOUCH)
        }
        return true
    }

    companion object {
        val TAG = "CustomScrollView"
    }
}