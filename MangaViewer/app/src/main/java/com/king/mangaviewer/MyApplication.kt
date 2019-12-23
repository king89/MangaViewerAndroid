package com.king.mangaviewer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import androidx.multidex.MultiDex
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.king.mangaviewer.di.AppComponent
import com.king.mangaviewer.di.DaggerAppComponent
import com.king.mangaviewer.domain.data.local.MangaDataBase
import com.king.mangaviewer.domain.repository.FavoriteMangaRepository
import com.king.mangaviewer.domain.repository.HistoryMangaRepository
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.viewmodel.AppViewModel
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
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

    @Inject
    lateinit var dataBase: MangaDataBase

    @Inject
    lateinit var favoriteMangaRepository: FavoriteMangaRepository

    @Inject
    lateinit var historyMangaRepository: HistoryMangaRepository

    val isMyAlarmRunning: Boolean
        get() = PendingIntent.getBroadcast(this, 0,
            Intent(this, AutoUpdateAlarmReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE) != null

    override fun onCreate() {
        super.onCreate()
        val core = CrashlyticsCore.Builder().build()
        Fabric.with(this, Crashlytics.Builder().core(core).build())
        setupRxExceptionHandler()
        //notify service
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val isStartService = sp.getBoolean(getString(R.string.pref_key_auto_update_service), true)
        if (isStartService && !isMyAlarmRunning) {
            val receiver = AutoUpdateAlarmReceiver()
            receiver.setAlarm(this)
        }
        MyApplication.context = applicationContext
        INSTANCE = this
        updateHashWhenVersion3()
    }

    private fun setupRxExceptionHandler() {
        RxJavaPlugins.setErrorHandler {
            Logger.e(TAG, it)
        }
    }

    @SuppressLint("CheckResult")
    private fun updateHashWhenVersion3() {
        if (dataBase.openHelper.writableDatabase.version == 3) {
            historyMangaRepository.updateAllHash()
                .andThen(favoriteMangaRepository.updateAllHash())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { Logger.i(TAG, "Updating url hash") },
                    { Logger.e(TAG, it) })

        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE: MyApplication

        const val TAG = "MyApplication"
    }
}
