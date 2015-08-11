package com.king.mangaviewer.actviity;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.king.mangaviewer.IViewFlipperControl;
import com.king.mangaviewer.R;
import com.king.mangaviewer.common.AsyncImageLoader;
import com.king.mangaviewer.common.Component.FitXImageView;
import com.king.mangaviewer.common.Component.MyViewFlipper;
import com.king.mangaviewer.common.util.MangaHelper.GetImageCallback;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

public class MangaPageActivity extends BaseActivity implements IViewFlipperControl {

    MyViewFlipper vFlipper = null;
    int mCurrPos = 0;
    LayoutInflater mInflater = null;
    List<MangaPageItem> pageList = null;

    private AsyncImageLoader asyncImageLoader = null;
    private boolean isFullScreen;
    View mDecorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected String getActionBarTitle() {
        // TODO Auto-generated method stub
        return this.getAppViewModel().Manga.getSelectedMangaChapterItem().getTitle();
    }

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // TODO: The system bars are visible. Make any desired
                    // adjustments to your UI, such as showing the action bar or
                    // other navigational controls.
                    isFullScreen = false;
                } else {
                    // TODO: The system bars are NOT visible. Make any desired
                    // adjustments to your UI, such as hiding the action bar or
                    // other navigational controls.
                    isFullScreen = true;
                }
            }
        });

        mInflater = LayoutInflater.from(this);
        asyncImageLoader = new AsyncImageLoader();

        setContentView(R.layout.activity_manga_page);
        vFlipper = (MyViewFlipper) this.findViewById(R.id.viewFlipper);
        vFlipper.setViewControl(this);
        fullScreen();
        mCurrPos = getAppViewModel().Manga.getNowPagePosition();
        if (getAppViewModel().Manga.getMangaPageList() == null) {
            new Thread() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    getPageList();
                }
            }.start();
        }
        else
        {
            pageList = getAppViewModel().Manga.getMangaPageList();
            setView(mCurrPos,mCurrPos);
        }
    }

    @Override
    protected void goBack() {
        getAppViewModel().Manga.setNowPagePosition(0);
        getAppViewModel().Manga.setMangaPageList(null);
        super.goBack();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void setView(int curr, int next) {
        View v = (View) mInflater.inflate(R.layout.list_manga_page_item, null);
        FitXImageView iv = (FitXImageView) v.findViewById(R.id.imageView);
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
                    .getDrawable(R.mipmap.ic_launcher);
            iv.setImageDrawable(tImage);
        }

        if (vFlipper.getChildCount() > 1) {
            vFlipper.removeViewAt(0);
        }
        vFlipper.addView(v, vFlipper.getChildCount());
        getAppViewModel().Manga.setNowPagePosition(next);
        mCurrPos = next;

    }

    public void movePrevious() {

        setView(mCurrPos, mCurrPos - 1);
        vFlipper.setInAnimation(this, R.anim.in_leftright);
        vFlipper.setOutAnimation(this, R.anim.out_leftright);
        vFlipper.showPrevious();
    }

    public void moveNext() {
        setView(mCurrPos, mCurrPos + 1);
        vFlipper.setInAnimation(this, R.anim.in_rightleft);
        vFlipper.setOutAnimation(this, R.anim.out_rightleft);
        vFlipper.showNext();
    }

    @Override
    public void scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    }

    @Override
    public void fling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

    }
    private void getPageList() {
        MangaViewModel mangaViewModel = this.getAppViewModel().Manga;
        pageList = this.getMangaHelper().GetPageList(
                mangaViewModel.getSelectedMangaChapterItem());
        mangaViewModel.setMangaPageList(pageList);
        handler.sendEmptyMessage(0);
    }

    public void fullScreen() {

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


}
