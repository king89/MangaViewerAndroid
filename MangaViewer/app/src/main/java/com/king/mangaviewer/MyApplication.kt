package com.king.mangaviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentProvider
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.king.mangaviewer.di.AppComponent
import com.king.mangaviewer.di.DaggerAppComponent
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver
import com.king.mangaviewer.util.MangaHelper
import com.king.mangaviewer.viewmodel.AppViewModel
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import javax.inject.Inject

class MyApplication : DaggerApplication() {
    lateinit var component: AppComponent
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build().apply {
            component = this
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    @Inject
    lateinit var appViewModel: AppViewModel

    val mangaHelper: com.king.mangaviewer.util.MangaHelper by lazy { MangaHelper(this) }

    val isMyAlarmRunning: Boolean
        get() = PendingIntent.getBroadcast(this, 0,
                Intent(this, AutoUpdateAlarmReceiver::class.java),
                PendingIntent.FLAG_NO_CREATE) != null

    override fun onCreate() {
        super.onCreate()
        val core = CrashlyticsCore.Builder().build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())

        //notify service
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val isStartService = sp.getBoolean(getString(R.string.pref_key_auto_update_service), true)
        if (isStartService && !isMyAlarmRunning) {
            val receiver = AutoUpdateAlarmReceiver()
            receiver.setAlarm(this)
        }
        MyApplication.context = applicationContext

    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var context: Context

    }
}
