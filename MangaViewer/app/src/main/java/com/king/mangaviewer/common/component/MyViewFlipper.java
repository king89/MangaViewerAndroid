package com.king.mangaviewer.common.Component;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import com.king.mangaviewer.IViewFlipperControl;

/**
 * Created by KinG on 8/10/2015.
 */
public class MyViewFlipper extends ViewFlipper {

    GestureDetector gestureDetector = null;
    IViewFlipperControl viewControl = null;

    public MyViewFlipper(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public MyViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(context, new GestureListener());

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return gestureDetector.onTouchEvent(ev);
    }



    public void setViewControl(IViewFlipperControl control) {
        this.viewControl = control;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onDown");
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY"
                    + velocityY);
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                int x = (int) (e2.getX() - e1.getX());
                if (x > 0) {
                    viewControl.movePrevious();
                } else {
                    viewControl.moveNext();
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
                    + distanceY);
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            viewControl.fullScreen();
            return super.onSingleTapConfirmed(e);
        }
    }
}
