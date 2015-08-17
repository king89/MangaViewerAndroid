package com.king.mangaviewer.actviity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.king.mangaviewer.R;
import com.king.mangaviewer.common.component.MyViewFlipper;

public class MangaPageActivity extends BaseActivity {

    MyViewFlipper vFlipper = null;
    private View mDecorView;
    SeekBar sb = null;

    ImageButton mFRImageButton, mFFImageButton;
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

        setContentView(R.layout.activity_manga_page);
        vFlipper = (MyViewFlipper) this.findViewById(R.id.viewFlipper);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);

        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int mControlsHeight = 0;
                int mShortAnimTime = 0;
                if (mControlsHeight == 0) {
                    mControlsHeight = controlsView.getHeight();
                }
                if (mShortAnimTime == 0) {
                    mShortAnimTime = getResources().getInteger(
                            android.R.integer.config_shortAnimTime);
                }
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // TODO: The system bars are visible. Make any desired
                    // adjustments to your UI, such as showing the action bar or
                    // other navigational controls.
//                    isFullScreen = false;
                    vFlipper.setFullScreen(false);
                    controlsView.animate()
                            .translationY(0)
                            .setDuration(mShortAnimTime);

                } else {
                    // TODO: The system bars are NOT visible. Make any desired
                    // adjustments to your UI, such as hiding the action bar or
                    // other navigational controls.
//                    isFullScreen = true;
                    vFlipper.setFullScreen(true);
                    controlsView.animate()
                            .translationY(mControlsHeight)
                            .setDuration(mShortAnimTime);
                }


            }
        });
        //ad
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //seekbar
        final TextView tv = (TextView) findViewById(R.id.textView_pageNum);
        sb = (SeekBar) findViewById(R.id.seekBar);
        sb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vFlipper.delayFullScreen();
                return false;
            }
        });
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv.setText("" + (progress + 1) + "/" + vFlipper.getPageCount());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setMax(vFlipper.getPageCount() - 1);
                seekBar.setProgress(vFlipper.getCurrPos());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int pos = seekBar.getProgress();
                vFlipper.goToPageNum(pos);
            }
        });
        //set current pos changed listener
        vFlipper.setOnCurrentPosChangedListener(new MyViewFlipper.OnCurrentPosChangedListener() {
            @Override
            public void onChanged(int pos) {
                sb.setProgress(pos);
                tv.setText("" + (pos + 1) + "/" + vFlipper.getPageCount());
            }
        });

        //Image Button
        mFFImageButton = (ImageButton)findViewById(R.id.ffButton);
        mFFImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFlipper.goNextChapter();
            }
        });
        mFRImageButton = (ImageButton)findViewById(R.id.frButton);
        mFRImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vFlipper.goPrevChapter();
            }
        });
        //start
        vFlipper.initial(getAppViewModel().Manga, getAppViewModel().Setting, handler, false);
    }

    @Override
    protected void update(Message msg) {
        this.getSupportActionBar().setTitle(getActionBarTitle());
    }

    @Override
    protected void goBack() {
        getAppViewModel().Manga.setNowPagePosition(0);
        getAppViewModel().Manga.setMangaPageList(null);
        super.goBack();
    }


    @Override
    protected boolean IsCanBack() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.page_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_setting) {

            View v = findViewById(R.id.menu_setting);
            displayPopupWindow(v);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.menu_page_setting, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.showAsDropDown(anchorView);
    }
}
