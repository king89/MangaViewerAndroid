package com.king.mangaviewer.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.MainActivity;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import org.joda.time.DateTime;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by KinG on 9/16/2015.
 */
public class AutoUpdateAlarmReceiver extends BroadcastReceiver {
    final public static String ONE_TIME = "onetime";
    public static final String AUTO_UPDATE_SERVICE = "AUTO_UPDATE_SERVICE";
    private NotificationManager nm;
    private Handler handler;

    @Override
    public void onReceive(final Context context, Intent intent) {
        handler = new Handler();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here.
        Bundle extras = intent.getExtras();
        StringBuilder msgStr = new StringBuilder();

        if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ");
        }
        Format formatter = new SimpleDateFormat("hh:mm:ss a");
        msgStr.append(formatter.format(new Date()));

        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
        Log.i("AlarmReceiver", msgStr.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkManga(context)) {
                    notifyFromHandler(context);
                }

            }
        }).start();
        //Release the lock
        wl.release();
    }


    private boolean checkManga(Context context) {
        Log.i("AutoNotify", "checkManga");
        FavouriteMangaDataSource dataSource = new FavouriteMangaDataSource(context);
        SettingViewModel svm = SettingViewModel.loadSetting(context);
        List<MangaWebSource> sources = svm.getMangaWebSources();
        List<FavouriteMangaMenuItem> flist = dataSource.getAllFavouriteMangaMenu(sources);

        MangaHelper helper = new MangaHelper(context);
        boolean isHaveUpdated = false;
        try {
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
        }catch (Exception e){
            e.printStackTrace();
        }
        svm.saveSetting(context);
        if (isHaveUpdated) {
            return true;
        } else {
            return false;
        }

    }

    private void notifyFromHandler(Context context) {
        nm = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AUTO_UPDATE_SERVICE, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                Intent.FLAG_ACTIVITY_NEW_TASK, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.msg_notify_updated_service_title))
                .setContentText(context.getString(R.string.msg_notify_updated_service_text))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_icon_pure)
                .setAutoCancel(true);
        ;
        nm.notify(0, builder.build());
    }

    public void setAlarm(Context context) {
        long t = context.getResources().getInteger(R.integer.auto_update_service_interval);
        //use for debug,if it -1, then use for release
        if (t < 0) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            int hour = Integer.parseInt(sp.getString(context.getResources().getString(R.string.pref_key_auto_update_hours), "6"));
            t = AlarmManager.INTERVAL_HOUR * hour;
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AutoUpdateAlarmReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        //After after t seconds
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), t, pi);
        Log.i("AlarmReceiver", "setAlarm:" + t);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AutoUpdateAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.i("AlarmReceiver", "cancelAlarm");
    }

    public void setOnetimeTimer(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AutoUpdateAlarmReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }
}
