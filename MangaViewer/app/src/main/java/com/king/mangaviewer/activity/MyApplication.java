package com.king.mangaviewer.activity;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver;
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
        if (isStartService && !isMyAlamRunning()) {
            AutoUpdateAlarmReceiver receiver = new AutoUpdateAlarmReceiver();
            receiver.setAlarm(this);
        }

    }

    public boolean isMyAlamRunning() {
        boolean alarmUp = (PendingIntent.getBroadcast(this, 0,
                new Intent(this, AutoUpdateAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
        return alarmUp;
    }
}