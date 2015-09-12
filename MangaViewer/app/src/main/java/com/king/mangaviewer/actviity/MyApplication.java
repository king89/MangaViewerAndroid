package com.king.mangaviewer.actviity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.service.AutoNotifyUpdatedService;
import com.king.mangaviewer.viewmodel.AppViewModel;

public class MyApplication extends Application {
    public com.king.mangaviewer.viewmodel.AppViewModel AppViewModel;

    public com.king.mangaviewer.common.util.SettingHelper SettingHelper;
    public com.king.mangaviewer.common.util.MangaHelper MangaHelper;

    public MyApplication() {
        super();
        // TODO Auto-generated constructor stub
        AppViewModel = new AppViewModel(this);
        MangaHelper = new MangaHelper(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppViewModel.Setting = AppViewModel.Setting.loadSetting(this);
        //notify service
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isStartService = sp.getBoolean(getString(R.string.pref_key_auto_update_service), true);
        if (isStartService) {
            startService(new Intent(this, AutoNotifyUpdatedService.class));
        }

    }

}
