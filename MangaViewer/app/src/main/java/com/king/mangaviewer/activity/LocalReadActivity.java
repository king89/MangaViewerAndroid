package com.king.mangaviewer.activity;

import android.os.Message;
import android.view.View;

import com.google.android.gms.ads.AdView;
import com.king.mangaviewer.R;

/**
 * Created by KinG on 7/15/2015.
 */
public class LocalReadActivity extends MangaPageActivity {

    @Override
    protected void initViewModels() {
        mMangaViewModel = getAppViewModel().LoacalManga;
        mSettingViewModel = getAppViewModel().Setting;
    }

    @Override
    protected void InitAd() {
        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.setVisibility(View.GONE);
        return;
    }

    @Override
    protected void update(Message msg) {
        this.getSupportActionBar().setTitle(getActionBarTitle());
        //add to history

    }
}