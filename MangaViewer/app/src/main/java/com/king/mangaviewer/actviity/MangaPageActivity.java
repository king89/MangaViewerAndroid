package com.king.mangaviewer.actviity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.king.mangaviewer.R;
import com.king.mangaviewer.R.layout;
import com.king.mangaviewer.common.AsyncImageLoader;
import com.king.mangaviewer.common.util.MangaHelper.GetImageCallback;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

public class MangaPageActivity extends BaseActivity implements OnTouchListener {

    ViewFlipper vFlipper = null;
    int mCurrPos = 0;
    LayoutInflater mInflater = null;
    List<MangaPageItem> pageList = null;
    GestureDetector gestureDetector = null;
    private AsyncImageLoader asyncImageLoader = null;
    private boolean isFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected String getActionBarTitle() {
        // TODO Auto-generated method stub
        return this.getAppViewModel().Manga.selectedMangaMenuItem.getTitle();
    }

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        mInflater = LayoutInflater.from(this);
        gestureDetector = new GestureDetector(this, new GestureListener());
        asyncImageLoader = new AsyncImageLoader();

        setContentView(R.layout.activity_manga_page);
        vFlipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
        vFlipper.setOnTouchListener(this);
        fullScreen();

        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                getPageList();
            }
        }.start();
    }

    @Override
    protected void update(Message msg) {
        // TODO Auto-generated method stub
        setView(mCurrPos, 0);

    }

    @Override
    protected boolean IsCanBack() {
        // TODO Auto-generated method stub
        return true;
    }

    private void setView(int curr, int next) {
        View v = (View) mInflater.inflate(R.layout.list_manga_page_item, null);
        ImageView iv = (ImageView) v.findViewById(R.id.imageView);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        // iv.setScaleType(ImageView.ScaleType.FIT_XY);
        if (curr < next && next > pageList.size() - 1)
            next = 0;
        else if (curr > next && next < 0)
            next = pageList.size() - 1;

        // iv.setImageResource(mImages[next]);
        String pageNum = (next + 1) + "/" + pageList.size();
        tv.setText(pageNum);

        String imagePath = this.pageList.get(next).getWebImageUrl();
        Drawable cachedImage = this.getMangaHelper().getPageImage(
                pageList.get(next), iv, new GetImageCallback() {

                    public void imageLoaded(Drawable imageDrawable,
                                            ImageView imageView, String imageUrl) {
                        // TODO Auto-generated method stub
                        if (imageDrawable != null && imageView != null) {
                            imageView.setImageDrawable(imageDrawable);
                        }

                    }
                });
        if (cachedImage != null) {
            iv.setImageDrawable(cachedImage);
        } else {
            Drawable tImage = getResources()
                    .getDrawable(R.drawable.ic_launcher);
            iv.setImageDrawable(tImage);
        }

        if (vFlipper.getChildCount() > 1) {
            vFlipper.removeViewAt(0);
        }
        vFlipper.addView(v, vFlipper.getChildCount());
        mCurrPos = next;

    }

    private void movePrevious() {
        setView(mCurrPos, mCurrPos - 1);
        vFlipper.setInAnimation(this, R.anim.in_leftright);
        vFlipper.setOutAnimation(this, R.anim.out_leftright);
        vFlipper.showPrevious();
    }

    private void moveNext() {
        setView(mCurrPos, mCurrPos + 1);
        vFlipper.setInAnimation(this, R.anim.in_rightleft);
        vFlipper.setOutAnimation(this, R.anim.out_rightleft);
        vFlipper.showNext();
    }

    private void getPageList() {
        MangaViewModel mangaViewModel = this.getAppViewModel().Manga;
        pageList = this.getMangaHelper().GetPageList(
                mangaViewModel.selectedMangaChapterItem);
        mangaViewModel.setMangaPageList(pageList);
        handler.sendEmptyMessage(0);
    }

    private void ToggleActionBar() {
        if (this.getActionBar().isShowing()) {
            this.getActionBar().hide();
        } else {
            this.getActionBar().show();
        }
    }

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureDetector.onTouchEvent(event);
    }

    private void fullScreen() {
        View mDecorView = getWindow().getDecorView();
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

    class GestureListener extends SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onDoubleTap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onDown");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY"
                    + velocityY);
            int x = (int) (e2.getX() - e1.getX());
            if (x > 0) {
                movePrevious();
            } else {
                moveNext();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onLongPress");
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
                    + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            fullScreen();
            return super.onSingleTapConfirmed(e);
        }
    }
}
