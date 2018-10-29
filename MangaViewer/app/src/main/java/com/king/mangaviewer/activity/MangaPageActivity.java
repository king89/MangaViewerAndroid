package com.king.mangaviewer.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.king.mangaviewer.R;
import com.king.mangaviewer.base.BaseActivity;
import com.king.mangaviewer.component.MyViewFlipper;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MangaPageActivity extends BaseActivity {

    public static final String INTENT_EXTRA_FROM_HISTORY = "intent_extra_from_history";

    public MyViewFlipper mViewFlipper = null;
    private View mDecorView;
    SeekBar sb = null;
    AdView mAdView;

    MangaViewModel mMangaViewModel;
    SettingViewModel mSettingViewModel;
    ImageButton mFRImageButton, mFFImageButton;
    Boolean mIsLoadFromHistory = false;

    Consumer<Object> mUpdateConsumer = new Consumer<Object>() {
        @Override
        public void accept(@NonNull Object o) throws Exception {
            update(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initViewModels();
        super.onCreate(savedInstanceState);
        //ad
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InitAd();
            }
        }, 5000);
    }

    protected void initViewModels() {
        mMangaViewModel = getAppViewModel().Manga;
        mSettingViewModel = getAppViewModel().Setting;
    }

    @Override
    protected String getActionBarTitle() {
        // TODO Auto-generated method stub
        return this.mMangaViewModel.getSelectedMangaChapterItem().getTitle();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mViewFlipper != null) {
            mViewFlipper.refresh();
        }
    }

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        mIsLoadFromHistory = getIntent().getBooleanExtra(INTENT_EXTRA_FROM_HISTORY, false);
        setContentView(R.layout.activity_manga_page);
        mViewFlipper = (MyViewFlipper) this.findViewById(R.id.viewFlipper);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);

        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
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
                            mViewFlipper.setFullScreen(false);
                            controlsView.animate()
                                    .translationY(0)
                                    .setDuration(mShortAnimTime);
                            controlsView.setVisibility(View.VISIBLE);
                            getSupportActionBar().show();

                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
//                    isFullScreen = true;
                            mViewFlipper.setFullScreen(true);

                            controlsView.animate()
                                    .translationY(mControlsHeight)
                                    .setDuration(mShortAnimTime);
                            controlsView.setVisibility(View.GONE);
                            getSupportActionBar().hide();
                        }

                    }
                });

        //seekbar
        final TextView tv = (TextView) findViewById(R.id.textView_pageNum);
        sb = (SeekBar) findViewById(R.id.seekBar);
        sb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mViewFlipper.delayFullScreen();
                return false;
            }
        });
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                tv.setText("" + (progress + 1) + "/" + mViewFlipper.getPageCount());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setMax(mViewFlipper.getPageCount() - 1);
                seekBar.setProgress(mViewFlipper.getCurrPos());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int pos = seekBar.getProgress();
                mViewFlipper.goToPageNum(pos);
            }
        });
        //set current pos changed listener
        mViewFlipper.setOnCurrentPosChangedListener(
                new MyViewFlipper.OnCurrentPosChangedListener() {
                    @Override
                    public void onChanged(int pos) {
                        sb.setProgress(pos);
                        sb.setMax(mViewFlipper.getPageCount() - 1);
                        tv.setText("" + (pos + 1) + "/" + mViewFlipper.getPageCount());
                    }
                });

        //Image Button
        mFFImageButton = (ImageButton) findViewById(R.id.ffButton);
        mFFImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.goNextChapter();
            }
        });
        mFRImageButton = (ImageButton) findViewById(R.id.frButton);
        mFRImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewFlipper.goPrevChapter();
            }
        });

        //start
        if (!mIsLoadFromHistory) {
            mViewFlipper.initial(mMangaViewModel, mSettingViewModel, mUpdateConsumer);
        } else {
            mViewFlipper.initialFromHistory(mMangaViewModel, mSettingViewModel, mUpdateConsumer);
        }

    }

    protected void InitAd() {

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
                super.onAdFailedToLoad(errorCode);
            }
        });

    }

    @Override
    protected void update(Message msg) {
        this.getSupportActionBar().setTitle(getActionBarTitle());
        //add to history
        this.getAppViewModel().HistoryManga.addChapterItemToHistory(
                mMangaViewModel.getSelectedMangaChapterItem());

    }

    @Override
    protected void goBack() {
        mMangaViewModel.setNowPagePosition(0);
        mMangaViewModel.setMangaPageList(null);
        super.goBack();
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

            mViewFlipper.stopAutoFullscreen();
            View v = findViewById(R.id.menu_setting);
            displayPopupWindow(v);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.menu_page_setting, null);

        //Init Switch
        SwitchCompat isFTRSwitch = (SwitchCompat) layout.findViewById(R.id.LTRSwitch);
        SwitchCompat splitPageSwitch = (SwitchCompat) layout.findViewById(R.id.splitPageSwitch);

        isFTRSwitch.setChecked(mSettingViewModel.getIsFromLeftToRight(this));
        isFTRSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettingViewModel.setIsFromLeftToRight(MangaPageActivity.this, isChecked);
                mViewFlipper.refresh();

            }
        });
        splitPageSwitch.setChecked(mSettingViewModel.getIsSplitPage(this));
        splitPageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettingViewModel.setIsSplitPage(MangaPageActivity.this, isChecked);
                mViewFlipper.refresh();
            }
        });

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

    @Override
    protected void onDestroy() {
        mViewFlipper.destroy();
        super.onDestroy();
    }
}
