package com.king.mangaviewer.component;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by KinG on 7/16/2015.
 */
@Deprecated
public class MangaImageSwitcher extends ImageSwitcher implements ViewSwitcher.ViewFactory, View.OnTouchListener {

    GestureDetector gestureDetector = null;
    private String filePath;
    private List<String> fileList;
    private int currPos;
    private int halfMode = 0; // 0:not 1:first half 2:second half
    private boolean isZoomed = false;
    private boolean isFullScreen;
    private boolean fromRightToLeft;

    public MangaImageSwitcher(Context context) {
        super(context);
    }

    public MangaImageSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void Initial(String fpath) {
        currPos = 0;
        fromRightToLeft = true;
        filePath = fpath;
        initialFile(fpath);
        gestureDetector = new GestureDetector(getContext(), new GestureListener());

        this.setFactory(this);
        this.setOnTouchListener(this);
        fullScreen();
        showImage();
    }

    private void initialFile(String fpath) {
        fileList = new ArrayList<String>();
        try {
            ZipEntry ze = null;
            ZipFile zp = new ZipFile(fpath);
            Enumeration<? extends ZipEntry> it = zp.entries();
            while (it.hasMoreElements()) {
                ze = it.nextElement();
                fileList.add(ze.getName());
                Log.v("loadManga", "" + ze.getSize());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View makeView() {
        ImageView iView = new ImageView(getContext());
        iView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iView.setLayoutParams(new LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iView.setBackgroundColor(0xFF000000);

        return iView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private void fullScreen() {
        View mDecorView = ((Activity) getContext()).getWindow().getDecorView();
        isFullScreen = !isFullScreen;
        if (isFullScreen) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
        } else {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    private void move(float distanceX, float distanceY) {
        if (isZoomed) {
            ImageView tim = ((ImageView) this.getCurrentView());
            Matrix m = tim.getImageMatrix();
            //TODO Set boundary
            m.postTranslate(-distanceX, -distanceY);
            tim.setImageMatrix(m);
            tim.invalidate();
        }
    }

    private void zoom() {
        ImageView tim = ((ImageView) this.getCurrentView());
        Matrix m = tim.getImageMatrix();
        if (!isZoomed) {
            m.postScale(2, 2);
            tim.setImageMatrix(m);

        } else {
            this.setImageDrawable(tim.getDrawable());
        }
        isZoomed = !isZoomed;

        tim.invalidate();
    }

    protected void moveNext() {
        Log.i("TEST", "MoveNext");
        if (!isZoomed) {

            if (currPos < fileList.size() - 1 && (halfMode == 0 || halfMode == 2))
                currPos++;
            if (halfMode == 1 || halfMode == 2)
                halfMode++;
            halfMode = halfMode % 3;
            showImage();
        }
    }

    protected void movePrevious() {
        Log.i("TEST", "MovePrev");
        if (!isZoomed) {
            if (currPos > 0 && (halfMode == 0 || halfMode == 1)) {
                currPos--;
            }
            if (halfMode == 1 || halfMode == 2) {
                halfMode--;
            }
            halfMode = halfMode % 3;
            showImage();
        }
    }

    protected void showImage() {
        ZipFile zf = null;
        try {
            zf = new ZipFile(filePath);
            ZipEntry ze = zf.getEntry(fileList.get(currPos));
            Bitmap img = BitmapFactory.decodeStream(zf.getInputStream(ze));
            int w = img.getWidth();
            int h = img.getHeight();
            if (halfMode == 0) {
                //make it half
                if (w > h) {
                    halfMode = 1;
                    img = Bitmap.createBitmap(img, w / 2, 0, w / 2, h);
                }
            } else if (halfMode == 1) {
                img = Bitmap.createBitmap(img, w / 2, 0, w / 2, h);
            } else if (halfMode == 2) {
                img = Bitmap.createBitmap(img, 0, 0, w / 2, h);
            }
            this.setImageDrawable(new BitmapDrawable(img));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            zoom();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            fullScreen();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
                    + distanceY);
            move(distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY"
                    + velocityY);
            int x = (int) (e2.getX() - e1.getX());
            if (fromRightToLeft) {
                x = -x;
            }
            if (x > 0) {
                movePrevious();
            } else {
                moveNext();
            }
            return false;
        }
    }
}
