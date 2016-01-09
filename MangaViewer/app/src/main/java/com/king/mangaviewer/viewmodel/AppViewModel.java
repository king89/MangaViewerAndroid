package com.king.mangaviewer.viewmodel;

import android.content.Context;

public class AppViewModel extends ViewModelBase {
    public MangaViewModel Manga;
    public SettingViewModel Setting;
    public LocalMangaViewModel LoacalManga;
    public HistoryViewModel HistoryManga;

    public AppViewModel(Context context) {
        mContext = context;
        this.Manga = new MangaViewModel();
        this.Setting = new SettingViewModel(mContext);
        this.LoacalManga = new LocalMangaViewModel();
        this.HistoryManga = new HistoryViewModel(mContext);
    }


}
