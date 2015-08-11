package com.king.mangaviewer.common.Component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES10;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by KinG on 8/1/2015.
 */
public class FitXImageView extends ImageView {
    private GestureDetectorCompat gestureDetector;
    private OverScroller overScroller;

    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;
    private static int maxBitmapSize;

    private float zoomFactor = 1;

    private int positionX = 0;
    private int positionY = 0;


    static {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        maxBitmapSize = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);

    }

    private boolean isLoadingImage;
    private static final int notZoomImageSize = 300;

    public FitXImageView(Context context) {
        super(context);

        // We will need screen dimensions to make sure we don't overscroll the
        // image
        DisplayMetrics dm = getResources().getDisplayMetrics();

        gestureDetector = new GestureDetectorCompat(context, gestureListener);
        overScroller = new OverScroller(context);
    }

    public FitXImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics dm = getResources().getDisplayMetrics();

        gestureDetector = new GestureDetectorCompat(context, gestureListener);
        overScroller = new OverScroller(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
        //return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();
        int h = ((BitmapDrawable) drawable).getBitmap().getHeight();
        int w = ((BitmapDrawable) drawable).getBitmap().getWidth();

        float hwFactor = h / (w * 1.0f);
        float whFactor = w / (h * 1.0f);
        if (w > maxBitmapSize || h > maxBitmapSize) {
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();

            if (w > h) {
                int tHeigth = (int) (this.getWidth() * hwFactor);
                int tWidth = this.getWidth();
                bm = Bitmap.createScaledBitmap(bm, tWidth, tHeigth, false);
                //set it center if image height < view height
                if (tHeigth < getHeight()) {
                    Matrix m = this.getImageMatrix();
                    m.postTranslate(0, (int) (0.5 * (getHeight() - tHeigth) + 0.5));
                    this.setImageMatrix(m);
                }
            } else {
                if ((this.getWidth() * hwFactor) > maxBitmapSize) {
                    // use matrix scale to fit width
                    int tHeigth = maxBitmapSize;
                    int tWidth = (int) (maxBitmapSize * whFactor);
                    bm = Bitmap.createScaledBitmap(bm, tWidth, tHeigth, false);
                    Matrix m = this.getImageMatrix();
                    zoomFactor = (getWidth() / (tWidth * 1.0f));
                    m.setScale(zoomFactor, zoomFactor);
                    this.setImageMatrix(m);
                } else {
                    bm = Bitmap.createScaledBitmap(bm, this.getWidth(), (int) (this.getWidth() * hwFactor), false);
                }
            }
            drawable = new BitmapDrawable(getResources(), bm);
            this.setScaleType(ScaleType.MATRIX);
        }
        this.setImageDrawable(drawable);
        fitX();
        super.onDraw(canvas);
    }

    private void fitX() {
        Drawable drawable = this.getDrawable();

        int h = (drawable).getBounds().height();
        int w = (drawable).getBounds().width();

        int sh = this.getHeight();
        int sw = this.getWidth();

        int bmH = ((BitmapDrawable) drawable).getBitmap().getHeight();
        int bmW = ((BitmapDrawable) drawable).getBitmap().getWidth();
        Matrix m = this.getImageMatrix();
        //dont use with loading image

        if(bmH <= notZoomImageSize || bmW <= notZoomImageSize) {
            m.setTranslate((int) (0.5 * (getWidth() - w) + 0.5), (int) (0.5 * (getHeight() - h) + 0.5));
            this.setImageMatrix(m);
        }
        else {
            zoomFactor = sw / (w * 1.0f);
            if (!isLoadingImage) {
                m.setScale(zoomFactor, zoomFactor);
            }
            //set it center if image height < view height
            int bmHeight = (int) (h * zoomFactor);
            if (bmHeight < getHeight()) {
                m.postTranslate(0, (int) (0.5 * (getHeight() - bmHeight) + 0.5));
                this.setImageMatrix(m);
            }

        }
        this.setImageMatrix(m);
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        // computeScrollOffset() returns true only when the scrolling isn't
        // already finished
        if (overScroller.computeScrollOffset()) {
            int oldX = this.getScrollX();
            int oldY = this.getScrollY();

            positionX = overScroller.getCurrX();
            positionY = overScroller.getCurrY();

            int max = getMaxVertical();
            if (positionY > max)
                positionY = max;
            scrollTo(positionX, positionY);
        }
        //this.postInvalidate();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        // Treat animating scrolls differently; see #computeScroll() for why.
        if (!overScroller.isFinished()) {
            super.scrollTo(scrollX, scrollY);

            if (clampedX || clampedY) {
                overScroller.springBack(0, this.getScrollY(), 0, 0, 0, getMaxVertical());
                Log.i("springBack", "scrollX:" + scrollX + "   scrollY:" + scrollY);
            }
            Log.i("onOverScrolled", "scrollX:" + scrollX + "   scrollY:" + scrollY);
            Log.i("onOverScrolled", "overScrollerX:" + overScroller.getCurrX() + "   overScrollerY:" + overScroller.getCurrY());
        } else {
            super.scrollTo(scrollX, scrollY);
        }
        awakenScrollBars();
    }

    private int getMaxHorizontal() {
//        return (getDrawable().getBounds().width() - this.getWidth());
        return 0;
    }

    private int getMaxVertical() {
        return Math.max(0, (int) ((getDrawable().getBounds().height() * zoomFactor) - this.getHeight()));
    }


    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {


        @Override
        public boolean onDown(MotionEvent e) {
            overScroller.forceFinished(true);
            ViewCompat.postInvalidateOnAnimation(FitXImageView.this);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            //overScroller.forceFinished(true);
            int max = (int) ((getDrawable().getBounds().height() * zoomFactor));
            overScroller.fling(positionX, positionY, (int) -velocityX,
                    (int) -velocityY, 0, getMaxHorizontal(), 0,
                    max);

            Log.i("onFling", "VelocityY: " + velocityY);
            invalidate();
            return false;
        }


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            overScroller.forceFinished(true);
            // normalize scrolling distances to not overscroll the image
            int dx = (int) distanceX;
            int dy = (int) distanceY;
            int newPositionX = positionX + dx;
            int newPositionY = positionY + dy;
            if (newPositionX < 0) {
                dx -= newPositionX;
            } else if (newPositionX > getMaxHorizontal()) {
                dx -= (newPositionX - getMaxHorizontal());
            }
            if (newPositionY < 0) {
                dy -= newPositionY;
            } else if (newPositionY > getMaxVertical()) {
                dy -= (newPositionY - getMaxVertical());
            }
            overScroller.startScroll(positionX, positionY, dx, dy, 0);
            ViewCompat.postInvalidateOnAnimation(FitXImageView.this);
            return true;
        }


    };
}
