package com.king.mangaviewer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.MainActivity;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by KinG on 9/6/2015.
 */
@Deprecated
public class AutoNotifyUpdatedService extends Service {
    public static final String AUTO_UPDATE_SERVICE = "AUTO_UPDATE_SERVICE";
    Timer timer;
    //once an hour
    private static final long ALERT_POLL_INTERVAL = 1 * 60 * 3600 * 1000;
    private NotificationManager nm;


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (checkManga()) {
                handler.sendEmptyMessage(0);
            }
        }
    };

    private boolean checkManga() {
        Log.i("AutoNotify","checkManga");
        FavouriteMangaDataSource dataSource = new FavouriteMangaDataSource(this);
        SettingViewModel svm = SettingViewModel.loadSetting(this);
        List<MangaWebSource> sources = svm.getMangaWebSources();
        List<FavouriteMangaMenuItem> flist = dataSource.getAllFavouriteMangaMenu(sources);

        MangaHelper helper = new MangaHelper(this.getApplicationContext());
        boolean isHaveUpdated = false;
        for (int i = 0; i < flist.size(); i++) {
            int chapterCount = flist.get(i).getChapterCount();
            int updatedCount = flist.get(i).getUpdateCount();

            List<MangaChapterItem> chlist = helper.getChapterList(flist.get(i));
            //have updated manga
            if (chlist.size() > chapterCount) {
                updatedCount = updatedCount + Math.max(0, chlist.size() - chapterCount);
                flist.get(i).setChapterCount(chlist.size());
                flist.get(i).setUpdateCount(updatedCount);
                flist.get(i).setUpdatedDate(DateTime.now().toString(FavouriteMangaMenuItem.DATE_FORMAT));
                dataSource.updateToFavourite(flist.get(i));
                isHaveUpdated = true;
            }
        }
        svm.saveSetting(this);
        if (isHaveUpdated) {
            return true;
        } else {
            return false;
        }

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            notifyFromHandler();
        }
    };

    private void notifyFromHandler() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AUTO_UPDATE_SERVICE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                Intent.FLAG_ACTIVITY_NEW_TASK, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.msg_notify_updated_service_title))
                .setContentText(getString(R.string.msg_notify_updated_service_text))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_icon_pure)
                .setAutoCancel(true);
        ;
        nm.notify(0, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AutoNotify", "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("AutoNotify", "Stop Service");
        timer.cancel();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("AutoNotify","Start Service");
        long t = getResources().getInteger(R.integer.auto_update_service_interval);
        //use for debug,if it -1, then use for release
        if (t < 0) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            int hour = Integer.parseInt(sp.getString(getResources().getString(R.string.pref_key_auto_update_hours), "6"));
            t = ALERT_POLL_INTERVAL * hour;
        }
        timer = new Timer();
        timer.schedule(task, t, t);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Log.i("AutoNotify","Schedule Time: " + t);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
