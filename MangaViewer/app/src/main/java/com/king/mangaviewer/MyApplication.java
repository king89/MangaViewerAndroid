package com.king.mangaviewer;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.Fragment;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.king.mangaviewer.di.DaggerAppComponent;
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver;
import com.king.mangaviewer.viewmodel.AppViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasContentProviderInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.fabric.sdk.android.Fabric;
import javax.inject.Inject;

public class MyApplication extends MultiDexApplication implements HasActivityInjector,
        HasServiceInjector, HasBroadcastReceiverInjector,
        HasContentProviderInjector {
    @Inject
    DispatchingAndroidInjector<Activity> activityInjector;
    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> broadcastReceiverInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;
    @Inject
    DispatchingAndroidInjector<Service> serviceInjector;
    @Inject
    DispatchingAndroidInjector<ContentProvider> contentProviderInjector;

    @Inject
    public AppViewModel appViewModel;
    public com.king.mangaviewer.util.SettingHelper SettingHelper;
    public com.king.mangaviewer.util.MangaHelper MangaHelper;


    private static Context context;

    public static Context getContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        DaggerAppComponent.builder().application(this).build().inject(this);

        super.onCreate();
        CrashlyticsCore core = new CrashlyticsCore.Builder().build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

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

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return activityInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return broadcastReceiverInjector;
    }

    @Override
    public AndroidInjector<ContentProvider> contentProviderInjector() {
        return contentProviderInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return serviceInjector;
    }
}
