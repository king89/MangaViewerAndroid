package com.king.mangaviewer.common.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES10;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import android.widget.OverScroller;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by KinG on 8/1/2015.
 */
public class FitXImageView extends ImageView {
    private static final float MAX_ZOOM = 3;
    private static final float MIN_ZOOM = 1;
    public static final double ZOOM_EXP = 1e-3;
    private GestureDetectorCompat gestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private OverScroller overScroller;
    public Matrix matrix = null;
    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;
    private static int maxBitmapSize;
    private static final int notZoomImageSize = 300;
    private PointF mFocusPoint = new PointF();
    private float fitZoomFactor = 1;
    private float matrixZoomFactor = 1;
    private float actualZoomFactor = 1;
    private int positionX = 0;
    private int positionY = 0;

    private boolean isZooming = false;
    private boolean toReset = false;
    boolean isFirstLoadedFinished = false;

    static {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        maxBitmapSize = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);

    }


    public FitXImageView(Context context) {
        super(context);
        // We will need screen dimensions to make sure we don't overscroll the
        // image
        init(context);
    }

    public FitXImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetectorCompat(context, gestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);
        overScroller = new OverScroller(context);
    }


    // Remember some things for zooming
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i("onTouchEvent", event.toString());
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    public void setIsFirstLoadedFinished(boolean b) {
        this.isFirstLoadedFinished = b;
    }

    public boolean isZoomed() {
        Log.i("IsZoom", "" + Math.abs(actualZoomFactor - fitZoomFactor));
        if (Math.abs(actualZoomFactor - fitZoomFactor) < ZOOM_EXP) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        setIsFirstLoadedFinished(false);
        super.setImageDrawable(drawable);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();

        if (!isFirstLoadedFinished) {
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
                        fitZoomFactor = (getWidth() / (tWidth * 1.0f));
                        m.setScale(fitZoomFactor, fitZoomFactor);
                        actualZoomFactor = fitZoomFactor;
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
            isFirstLoadedFinished = true;
        }
        //Log.i("Zoom Factor", "act:" + actualZoomFactor + " matrix:" + matrixZoomFactor);
        if (isZooming) {
            matrix = new Matrix();
            zoom(matrix, actualZoomFactor);
            zoom(matrix, matrixZoomFactor);
            this.setImageMatrix(matrix);
        }
        setMid();
        super.onDraw(canvas);
    }

    private void translation(Matrix matrix, float x, float y) {
        float[] values = new float[9];
        matrix.getValues(values);

        values[Matrix.MTRANS_X] = x;
        values[Matrix.MTRANS_Y] = y;

        matrix.setValues(values);
    }

    private void zoom(Matrix matrix, float zoomFactor) {
        float[] values = new float[9];
        matrix.getValues(values);
        float sx = values[Matrix.MSCALE_X];
        float sy = values[Matrix.MSCALE_Y];
        sx = sx * zoomFactor;
        sy = sy * zoomFactor;

        values[Matrix.MSCALE_X] = sx;
        values[Matrix.MSCALE_Y] = sy;

        matrix.setValues(values);
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

        if (bmH <= notZoomImageSize && bmW <= notZoomImageSize) {
            m.setTranslate((int) (0.5 * (getWidth() - w) + 0.5), (int) (0.5 * (getHeight() - h) + 0.5));
            this.setImageMatrix(m);
        } else {
            actualZoomFactor = fitZoomFactor = sw / (w * 1.0f);
            m.setScale(fitZoomFactor, fitZoomFactor);
            //set it center if image height < view height
            int bmHeight = (int) (h * fitZoomFactor);
            if (bmHeight < getHeight()) {
                m.postTranslate(0, (int) (0.5 * (getHeight() - bmHeight) + 0.5));
                this.setImageMatrix(m);
            }

        }
        this.setImageMatrix(m);
    }

    private void setMid() {
        Matrix matrix = this.getImageMatrix();
        Drawable drawable = this.getDrawable();

        int h = (int) ((drawable).getBounds().height() * actualZoomFactor * matrixZoomFactor);
        int w = (int) ((drawable).getBounds().width() * actualZoomFactor * matrixZoomFactor);

        int bmH = ((BitmapDrawable) drawable).getBitmap().getHeight();
        int bmW = ((BitmapDrawable) drawable).getBitmap().getWidth();
        float[] values = new float[9];
        matrix.getValues(values);
        if (h <= this.getHeight()) {
            int y = (int) (0.5 * (getHeight() - h) + 0.5);
            values[Matrix.MTRANS_Y] = y;

        }
        if (w <= this.getWidth()) {
            int x = (int) (0.5 * (getWidth() - w) + 0.5);
            values[Matrix.MTRANS_X] = x;
        }
        matrix.setValues(values);
        this.setImageMatrix(matrix);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        // computeScrollOffset() returns true only when the scrolling isn't
        // already finished
        if (overScroller.computeScrollOffset()) {

            if (!isZooming) {
                tposX = overScroller.getCurrX();
                tposY = overScroller.getCurrY();
                if (toReset) {
                    tposX = 0;
                    tposY = 0;
                    toReset = false;
                }
            }
            positionX = (int) ((tposX + mFocusPoint.x) * matrixZoomFactor - mFocusPoint.x);
            positionY = (int) ((tposY + mFocusPoint.y) * matrixZoomFactor - mFocusPoint.y);
            int maxY = (int) (getMaxVertical());
            int maxX = (int) (getMaxHorizontal());

            if (positionX > maxX) {
                positionX = maxX;
            } else if (positionX < 0) {
                positionX = 0;
            }

            if (positionY > maxY) {
                positionY = maxY;
            } else if (positionY < 0) {
                positionY = 0;
            }

            scrollTo(positionX, positionY);
//            Log.i("computeScroll", "overScroller.getCurrX(): " + overScroller.getCurrX() + " overScroller.getCurrY():" + overScroller.getCurrY());
//            Log.i("computeScroll", "positionX: " + positionX + " positionY:" + positionY);
        } else {
            //Log.i("computeScroll / else", "positionX: " + positionX + " positionY:" + positionY);
            overScroller.springBack(this.getScrollX(), this.getScrollY(), 0, getMaxHorizontal(), 0, getMaxVertical());

        }

    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        // Treat animating scrolls differently; see #computeScroll() for why.
        if (!overScroller.isFinished()) {
            super.scrollTo(scrollX, scrollY);

            if (clampedX || clampedY) {
                overScroller.springBack(this.getScrollX(), this.getScrollY(), 0, getMaxHorizontal(), 0, getMaxVertical());
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
        return (Math.max(0, (int) (getDrawable().getBounds().width() * actualZoomFactor * matrixZoomFactor) - this.getWidth()));
    }

    private int getMaxVertical() {
        return Math.max(0, (int) ((getDrawable().getBounds().height() * actualZoomFactor * matrixZoomFactor) - this.getHeight()));
    }


    private void resetImage() {
        actualZoomFactor = fitZoomFactor;
        matrixZoomFactor = 1;

        matrix = new Matrix();
        zoom(matrix, actualZoomFactor);
        zoom(matrix, matrixZoomFactor);
        this.setImageMatrix(matrix);
        toReset = true;
        scrollTo(0, 0);

        this.postInvalidate();
    }

    private int tposX, tposY;
    private ScaleGestureDetector.SimpleOnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            overScroller.forceFinished(true);
            mFocusPoint.set(detector.getFocusX(), detector.getFocusY());
            isZooming = false;
            matrixZoomFactor = 1;
            tposX = overScroller.getCurrX();
            tposY = overScroller.getCurrY();

            Log.i("onScaleBegin", " focusX:" + detector.getFocusX() + " focusY:" + detector.getFocusY());
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.i("onScaleEnd", "onScaleEnd");
            overScroller.forceFinished(true);
            isZooming = false;
            actualZoomFactor = matrixZoomFactor * actualZoomFactor;
            matrixZoomFactor = 1;
            mFocusPoint.set(0, 0);

            super.onScaleEnd(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isZooming = true;
            overScroller.forceFinished(true);
            StringBuilder sb = new StringBuilder();
            matrixZoomFactor = detector.getScaleFactor();
            if (matrixZoomFactor * actualZoomFactor < MIN_ZOOM * fitZoomFactor) {
                matrixZoomFactor = MIN_ZOOM / actualZoomFactor * fitZoomFactor;
            }
            if (matrixZoomFactor * actualZoomFactor > MAX_ZOOM * fitZoomFactor) {
                matrixZoomFactor = MAX_ZOOM / actualZoomFactor * fitZoomFactor;
            }

//
//            sb.append(" dx:" + dx + " dy:" + dy);
//            sb.append(" tx:" + tx + " ty:" + ty);
//            sb.append(" positionX:" + positionX + " positionY:" + positionY);
//            sb.append(" focusX:" + detector.getFocusX() + " focusY:" + detector.getFocusY());

//            Log.i("onScale", sb.toString());
            return super.onScale(detector);
        }


    };
    private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            resetImage();
            return super.onDoubleTap(e);
        }

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
            int max = (int) ((getDrawable().getBounds().height() * actualZoomFactor));
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
            Log.i("onScroll", "positionX: " + positionX + " positionY:" + positionY);
            overScroller.startScroll(positionX, positionY, dx, dy, 0);
            ViewCompat.postInvalidateOnAnimation(FitXImageView.this);
            return true;
        }


    };


}
