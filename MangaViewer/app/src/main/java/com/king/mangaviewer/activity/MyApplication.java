package com.king.mangaviewer.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.king.mangaviewer.R;
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver;
import com.king.mangaviewer.util.MangaHelper;
import com.king.mangaviewer.viewmodel.AppViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends MultiDexApplication {
    public com.king.mangaviewer.viewmodel.AppViewModel AppViewModel;

    public com.king.mangaviewer.util.SettingHelper SettingHelper;
    public com.king.mangaviewer.util.MangaHelper MangaHelper;

    public MyApplication() {
        super();
        // TODO Auto-generated constructor stub
        AppViewModel = new AppViewModel(this);
        MangaHelper = new MangaHelper(this);

    }

    private static Context context;

    public static Context getContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        AppViewModel.Setting = SettingViewModel.loadSetting(this);
        //notify service
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isStartService = sp.getBoolean(getString(R.string.pref_key_auto_update_service), true);
        if (isStartService && !isMyAlarmRunning()) {
            AutoUpdateAlarmReceiver receiver = new AutoUpdateAlarmReceiver();
            receiver.setAlarm(this);
        }
        MyApplication.context = getApplicationContext();
    }

    public boolean isMyAlarmRunning() {
        return (PendingIntent.getBroadcast(this, 0,
                new Intent(this, AutoUpdateAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);
    }
}
