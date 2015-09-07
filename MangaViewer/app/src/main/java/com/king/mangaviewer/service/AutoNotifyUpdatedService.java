package com.king.mangaviewer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.king.mangaviewer.R;
import com.king.mangaviewer.actviity.MainActivity;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by KinG on 9/6/2015.
 */
public class AutoNotifyUpdatedService extends Service {
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
        SettingViewModel sv = SettingViewModel.loadSetting(this);
        List<FavouriteMangaMenuItem> flist = new ArrayList<>(sv.getFavouriteMangaList());
        MangaHelper helper = new MangaHelper(this);
        boolean isHaveUpdated = false;
        for (int i = 0; i < flist.size(); i++) {
            int chapterCount = flist.get(i).getChapterCount();
            int updatedCount = flist.get(i).getUpdateCount();

            List<MangaChapterItem> chlist = helper.getChapterList(flist.get(i));
            //have updated manga
            if (chlist.size() > chapterCount) {
                updatedCount = updatedCount + (chlist.size() - chapterCount);
                flist.get(i).setChapterCount(chlist.size());
                flist.get(i).setUpdateCount(updatedCount);
                sv.addFavouriteManga(flist.get(i));
                isHaveUpdated = true;
            }
        }

        if (isHaveUpdated) {
            sv.saveSetting(this);
            return true;
        }
        else {
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
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                Intent.FLAG_ACTIVITY_NEW_TASK, new Intent(this, MainActivity.class),
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
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        timer.schedule(task, 5000, ALERT_POLL_INTERVAL);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
