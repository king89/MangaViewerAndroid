package com.king.mangaviewer.viewmodel;


import android.content.Context;

import com.king.mangaviewer.actviity.MyApplication;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.util.SettingHelper;

import java.util.Set;

public class SettingViewModel extends ViewModelBase {

    private Constants.WebSiteEnum mSelectedWebSite;
    private String mDefaultLocalMangaPath;

    public static SettingViewModel loadSetting(Context context) {
        SettingViewModel svm = SettingHelper.loadSetting(context);
        if (svm.mSelectedWebSite == null) {
            svm.mSelectedWebSite = Constants.WebSiteEnum.HHComic;
            svm.mDefaultLocalMangaPath = "/Books/Manga";
            return svm;
        } else {
            return svm;
        }
    }

    public Constants.WebSiteEnum getSelectedWebSite() {
        return mSelectedWebSite;
    }

    public void setSelectedWebSite(Constants.WebSiteEnum webSite) {
        mSelectedWebSite = webSite;
    }

    public String getDefaultLocalMangaPath() {
        return mDefaultLocalMangaPath;
    }

    public void setDefaultLocalMangaPath(String path) {
        mDefaultLocalMangaPath = path;
    }

    public void saveSetting(Context context) {
        SettingHelper.saveSetting(context, this);
    }
}
