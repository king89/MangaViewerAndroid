package com.king.mangaviewer;

import android.view.MotionEvent;

/**
 * Created by KinG on 8/10/2015.
 */
public interface IViewFlipperControl {

    public void moveNext();

    public void movePrevious();

    public void scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    public void fling(MotionEvent e1, MotionEvent e2, float velocityX,
                      float velocityY);

    public void fullScreen();
}
