package com.king.mangaviewer.common.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.king.mangaviewer.R;
import com.king.mangaviewer.actviity.BaseActivity;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.List;

/**
 * Created by KinG on 8/10/2015.
 */
public class MyViewFlipper extends ViewFlipper {

    GestureDetector gestureDetector = null;
    List<MangaPageItem> pageList = null;
    LayoutInflater mInflater = null;
    MangaViewModel mangaViewModel = null;
    SettingViewModel settingViewModel = null;
    protected boolean isFullScreen;
    View mDecorView;
    OnCurrentPosChangedListener mCurrentPosChangedListener;
    int mCurrPos;

    int animatePreInId;
    int animatePreOutId;

    int animateNextInId;
    int animateNextOutId;

    boolean goNextChapter = false;
    boolean goPrevChapter = false;

    boolean orderDesc = true;

    Handler updateHandler;
    Handler mHideHandler;
    Runnable mHideRunnable;

    protected final int delayMillis = 3000;

    protected int halfMode = 0; // 0:not 1:first half 2:second half


    protected final Object lock = new Object();

    public MyViewFlipper(Context context) {
        super(context);
        initControl();
    }

    public MyViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        initControl();
    }

    public void setFullScreen(boolean b) {
        isFullScreen = b;
        fullScreen();
    }

    protected void initControl() {

        mInflater = LayoutInflater.from(getContext());
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        mDecorView = ((Activity) getContext()).getWindow().getDecorView();


        mHideHandler = new Handler();
        mHideRunnable = new Runnable() {
            @Override
            public void run() {
                setFullScreen(true);
            }
        };


    }

    protected void initial() {
        pageList = null;
        halfMode = 0;
        this.removeAllViews();
        mangaViewModel.setMangaPageList(null);
        initial(mangaViewModel, settingViewModel, updateHandler);
    }

    public void initial(MangaViewModel mvm, SettingViewModel svm, Handler handler) {
        mangaViewModel = mvm;
        settingViewModel = svm;
        updateHandler = handler;
        initPageAnimation();
        delayFullScreen();

        if (mangaViewModel.getMangaPageList() == null) {
            new Thread() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    getPageList();
                }
            }.start();
        } else {
            pageList = mangaViewModel.getMangaPageList();
            setView(getCurrPos(), getCurrPos());
        }
    }


    protected boolean getIsSplitPage() {
        if (settingViewModel != null) {
            return settingViewModel.getIsSplitPage(getContext());
        } else {
            return true;
        }

    }

    public int getPageCount() {
        if (pageList != null) {
            return this.pageList.size();
        } else {
            return 0;
        }
    }

    public void delayFullScreen() {
        setFullScreen(false);
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void setOnCurrentPosChangedListener(OnCurrentPosChangedListener l) {
        this.mCurrentPosChangedListener = l;
    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setView(getCurrPos(), getCurrPos());
            updateHandler.sendEmptyMessage(0);
        }
    };

    public void stopAutoFullscreen() {
        mHideHandler.removeCallbacks(mHideRunnable);
    }

    protected boolean canSplitPage() {
        if (this.getContext().getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE &&
                getIsSplitPage()) {
            return true;
        } else {
            return false;
        }
    }

    protected void getPageList() {
        pageList = getBaseActivty().getMangaHelper().GetPageList(
                mangaViewModel.getSelectedMangaChapterItem());
        mangaViewModel.setMangaPageList(pageList);

        handler.sendEmptyMessage(0);
    }

    public boolean getOrderDesc() {
        return orderDesc;
    }

    public int getCurrPos() {
        mCurrPos = mangaViewModel.getNowPagePosition();
        return mCurrPos;
    }

    public void setCurrPos(int mCurrPos) {
        this.mCurrPos = mCurrPos;
        mangaViewModel.setNowPagePosition(mCurrPos);
        if (mCurrentPosChangedListener != null) {
            mCurrentPosChangedListener.onChanged(mCurrPos);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return gestureDetector.onTouchEvent(ev);
    }

    protected BaseActivity getBaseActivty() {
        return ((BaseActivity) getContext());
    }

    public void initPageAnimation() {
        if (getIsFromLeftToRight()) {
            animatePreInId = R.anim.in_rightleft;
            animatePreOutId = R.anim.out_rightleft;

            animateNextInId = R.anim.in_leftright;
            animateNextOutId = R.anim.out_leftright;
        } else {
            animatePreInId = R.anim.in_leftright;
            animatePreOutId = R.anim.out_leftright;

            animateNextInId = R.anim.in_rightleft;
            animateNextOutId = R.anim.out_rightleft;

        }
    }

    public void refresh() {
        initPageAnimation();
        halfMode = 0;
        setView(getCurrPos(), getCurrPos());
        this.showNext();
    }

    public void goToPageNum(int num) {
        if (pageList != null && num >= 0 && num <= this.pageList.size() - 1) {
            halfMode = 0;
            int t = getCurrPos();
            setCurrPos(num);
            setView(t, num);
            this.showPrevious();
        }
    }

    protected void setView(final int curr, int next) {
        View v = (View) mInflater.inflate(R.layout.list_manga_page_item, null);
        FitXImageView iv = (FitXImageView) v.findViewById(R.id.imageView);
        // iv.setScaleType(ImageView.ScaleType.FIT_XY);
        if (curr < next && next > pageList.size() - 1)
            next = 0;
        else if (curr > next && next < 0)
            next = pageList.size() - 1;

        // iv.setImageResource(mImages[next]);

        final int fnext = next;
        synchronized (lock) {
            setCurrPos(next);
        }
        Drawable cachedImage = getBaseActivty().getMangaHelper().getPageImage(
                pageList.get(next), iv, new MangaHelper.GetImageCallback() {

                    public void imageLoaded(Drawable imageDrawable,
                                            ImageView imageView, String imageUrl) {
                        // TODO Auto-generated method stub
                        if (imageDrawable != null && imageView != null) {
                            //imageView.setImageDrawable(imageDrawable);
                            showImage(imageView, imageDrawable, curr, fnext);
                        }

                    }
                });
        if (cachedImage != null) {
            showImage(iv, cachedImage, curr, next);
        } else {
            Drawable tImage = getResources()
                    .getDrawable(R.mipmap.ic_preloader_background);
            iv.setImageDrawable(tImage);
        }

        if (this.getChildCount() > 1) {
            this.removeViewAt(0);
        }
        this.addView(v, this.getChildCount());


        goPrevChapter = false;
        goNextChapter = false;
    }

    protected void showImage(ImageView iv, Drawable d, int curr, int next) {
        if (canSplitPage()) {
            Bitmap img = ((BitmapDrawable) d).getBitmap();
            int w = img.getWidth();
            int h = img.getHeight();
            //ensure this image should be shown
            synchronized (lock) {
                if (next == mCurrPos) {
                    //determine the half 1 part with "isFromLeftToRight"
                    int halfPartTwo, halfPartOne;
                    if (getIsFromLeftToRight()) {
                        halfPartOne = w / 2;
                        halfPartTwo = 0;
                    } else {
                        halfPartOne = 0;
                        halfPartTwo = w / 2;

                    }
                    if (halfMode == 0) {
                        //make it half
                        if (w > h) {
                            if (curr > next) {
                                halfMode = 2;
                                img = Bitmap.createBitmap(img, halfPartTwo, 0, w / 2, h);
                            } else {
                                halfMode = 1;
                                img = Bitmap.createBitmap(img, halfPartOne, 0, w / 2, h);
                            }

                        }
                    } else if (halfMode == 1) {
                        img = Bitmap.createBitmap(img, halfPartOne, 0, w / 2, h);
                    } else if (halfMode == 2) {
                        img = Bitmap.createBitmap(img, halfPartTwo, 0, w / 2, h);
                    }
                }
            }
            iv.setImageDrawable(new BitmapDrawable(img));
        } else {
            iv.setImageDrawable(d);
        }
    }

    protected void movePrevious() {

        if (mCurrPos - 1 < 0) {
            showFirstOrLastPageTips();
        } else {
            setView(mCurrPos, mCurrPos - 1);
            this.showPrevious();
        }
    }

    protected void movePreviousHalf() {
        if (mCurrPos - 1 < 0 && (halfMode == 0 || halfMode == 1)) {
            showFirstOrLastPageTips();
        } else {
            int next = mCurrPos;
            if (mCurrPos > 0 && (halfMode == 0 || halfMode == 1)) {
                next--;
            }
            if (halfMode == 1 || halfMode == 2) {
                halfMode--;
            }
            halfMode = halfMode % 3;

            setView(mCurrPos, next);
            this.showPrevious();
        }
    }

    protected void moveNextHalf() {
        if (mCurrPos + 1 > pageList.size() - 1 && (halfMode == 0 || halfMode == 2)) {
            showFirstOrLastPageTips();
        } else {
            int next = mCurrPos;
            if (mCurrPos < pageList.size() - 1 && (halfMode == 0 || halfMode == 2)) {
                next++;
            }
            if (halfMode == 1 || halfMode == 2) {
                halfMode++;
            }
            halfMode = halfMode % 3;

            setView(mCurrPos, next);
            this.showNext();
        }
    }

    protected void moveNext() {

        if (mCurrPos + 1 > pageList.size() - 1) {
            showFirstOrLastPageTips();
        } else {
            setView(mCurrPos, mCurrPos + 1);
            this.showNext();
        }
    }

    protected void showFirstOrLastPageTips() {
        if (mCurrPos == 0) {
            if (goPrevChapter) {
                goPrevChapter();
                return;
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.first_page), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), getResources().getString(R.string.first_page_again), Toast.LENGTH_SHORT).show();
                goPrevChapter = true;
            }
        }
        if (mCurrPos == pageList.size() - 1) {
            if (goNextChapter) {
                goNextChapter();
                return;
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.last_page), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getContext(), getResources().getString(R.string.last_page_again), Toast.LENGTH_SHORT).show();
                goNextChapter = true;
            }
        }
    }

    public void goPrevChapter() {
        int index = mangaViewModel.getMangaChapterList().indexOf(mangaViewModel.getSelectedMangaChapterItem());
        if (getOrderDesc() && index + 1 < mangaViewModel.getMangaChapterList().size()) {
            mangaViewModel.setSelectedMangaChapterItem(index + 1);
            this.initial();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_more_prev_chapter), Toast.LENGTH_SHORT).show();
        }
    }

    public void goNextChapter() {
        int index = mangaViewModel.getMangaChapterList().indexOf(mangaViewModel.getSelectedMangaChapterItem());
        if (getOrderDesc() && index - 1 >= 0) {
            mangaViewModel.setSelectedMangaChapterItem(index - 1);
            this.initial();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.no_more_next_chapter), Toast.LENGTH_SHORT).show();
        }
    }

    protected void fullScreen() {
        if (isFullScreen) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
            //show page slider

        } else {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    public boolean getIsFromLeftToRight() {
        if (settingViewModel != null) {
            return settingViewModel.getIsFromLeftToRight();
        } else {
            return true;
        }
    }

    public interface OnCurrentPosChangedListener {
        public void onChanged(int pos);
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
                    + velocityY + " Tangle :" + Math.toDegrees(Math.atan(velocityY / velocityX)));

            FitXImageView fiv = (FitXImageView)getCurrentView().findViewById(R.id.imageView);;
            if (fiv.isZoomed())
            {
                return false;
            }

            double maxDegree = 30;
            if (Math.abs(velocityX) > Math.abs(velocityY) && Math.abs(Math.toDegrees(Math.atan(velocityY / velocityX))) < maxDegree) {
                int x = (int) (e2.getX() - e1.getX());
                if (getIsFromLeftToRight()) {
                    x = -x;
                }
                if (x > 0) {
                    MyViewFlipper.this.setInAnimation(MyViewFlipper.this.getContext(), animatePreInId);
                    MyViewFlipper.this.setOutAnimation(MyViewFlipper.this.getContext(), animatePreOutId);
                    if (canSplitPage()) {
                        movePreviousHalf();
                    } else {
                        movePrevious();
                    }
                } else {
                    MyViewFlipper.this.setInAnimation(MyViewFlipper.this.getContext(), animateNextInId);
                    MyViewFlipper.this.setOutAnimation(MyViewFlipper.this.getContext(), animateNextOutId);
                    if (canSplitPage()) {
                        moveNextHalf();
                    } else {
                        moveNext();
                    }
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
//            Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = "
//                    + distanceY);
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!isFullScreen) {
                setFullScreen(true);
            } else {
                delayFullScreen();
            }
            return super.onSingleTapConfirmed(e);
        }
    }
}
