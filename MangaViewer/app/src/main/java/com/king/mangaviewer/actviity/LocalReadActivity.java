package com.king.mangaviewer.actviity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.google.android.gms.ads.AdView;
import com.king.mangaviewer.R;
import com.king.mangaviewer.common.component.MangaImageSwitcher;

import java.util.ArrayList;

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
}