package com.king.mangaviewer.actviity;

import android.app.Application;

import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.viewmodel.AppViewModel;

public class MyApplication extends Application {
    public com.king.mangaviewer.viewmodel.AppViewModel AppViewModel;

    public com.king.mangaviewer.common.util.SettingHelper SettingHelper;
    public com.king.mangaviewer.common.util.MangaHelper MangaHelper;

    public MyApplication() {
        super();
        // TODO Auto-generated constructor stub
        AppViewModel = new AppViewModel();
        MangaHelper = new MangaHelper(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppViewModel.Setting = AppViewModel.Setting.loadSetting(this);
    }

}
