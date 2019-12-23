package com.king.mangaviewer.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.PowerManager
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.king.mangaviewer.R
import com.king.mangaviewer.common.Constants
import com.king.mangaviewer.di.RepositoryModule
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.ui.main.MainActivity
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import com.king.mangaviewer.util.NotificationHelper
import com.king.mangaviewer.viewmodel.SettingViewModel
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by KinG on 9/16/2015.
 */
class AutoUpdateAlarmReceiver : BroadcastReceiver() {
    private var nm: NotificationManager? = null
    private var handler: Handler? = null
    private var updatedNames: String? = null

    override fun onReceive(context: Context, intent: Intent) {
        handler = Handler()
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AutoUpdateAlarmReceiver:")
        //Acquire the lock
        wl.acquire(10 * 60 * 1000L /*10 minutes*/)

        //You can do the processing here.
        val extras = intent.extras
        val msgStr = StringBuilder()

        if (extras != null && extras.getBoolean(ONE_TIME, java.lang.Boolean.FALSE)) {
            //Make sure this intent has been sent by the one-time timer button.
            msgStr.append("One time Timer : ")
        }
        val formatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        msgStr.append(formatter.format(Date()))

        Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show()
        Log.i(TAG, msgStr.toString())

        Thread(Runnable {
            if (checkManga(context)) {
                notifyFromHandler(context)
            }
        }).start()
        //Release the lock
        wl.release()
    }

    private fun checkManga(context: Context): Boolean {
        Log.i(TAG, "checkManga")
        val delayTimeMillis = 5000L
        val dataSource = RepositoryModule.provideDb(context).favouriteMangaDAO()
        val svm = SettingViewModel.loadSetting(context)
        val sources = svm.mangaWebSources
        val dataList = dataSource.getFavouriteList().blockingGet()
        val sb = StringBuilder()
        var isHaveUpdated = false
        try {
            for (item in dataList) {
                val chapterCount = item.chapter_count
                var updatedCount = item.update_count
                val source = sources.firstOrNull { it.id == item.manga_websource_id }!!
                val menu = MangaMenuItem(item.hash, item.title, item.description, item.imagePath,
                    item.url, source)
                //TODO change MangaHelperV2 to inject factory
                val chlist = MangaHelperV2.getChapterList(menu)
                //have updated manga
                if (chlist.size > chapterCount) {
                    updatedCount += Math.max(0, chlist.size - chapterCount)
                    item.chapter_count = chlist.size
                    item.update_count = updatedCount
                    item.updated_date = DateTime.now().toString(
                        Constants.DATE_FORMAT_LONG)
                    dataSource.update(item)
                    sb.append(item.title + ", ")
                    isHaveUpdated = true
                }
                Thread.sleep(delayTimeMillis)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.e(TAG, e, e.message ?: "")
        }

        svm.saveSetting(context)
        return if (isHaveUpdated) {
            updatedNames = sb.toString()
            true
        } else {
            updatedNames = ""
            false
        }

    }

    private fun notifyFromHandler(context: Context) {
        NotificationHelper.createNotificationChannel(context)

        nm = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(AUTO_UPDATE_SERVICE, true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        val pendingIntent = PendingIntent.getActivity(context,
            Intent.FLAG_ACTIVITY_NEW_TASK, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val updatedContentText = updatedNames
        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(updatedContentText)
        val summary = (updatedNames?.split(",")?.size?.minus(1) ?: 0).let {
            context.getString(R.string.manga_updated_count, it)
        }

        val builder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setContentTitle(summary)
            .setContentIntent(pendingIntent)
            .setStyle(bigText)
            .setSmallIcon(R.mipmap.ic_icon_pure)
            .setAutoCancel(true)
        nm!!.notify(0, builder.build())
    }

    fun setAlarm(context: Context) {
        var t = context.resources.getInteger(R.integer.auto_update_service_interval).toLong()
        //use for debug,if it -1, then use for release
        if (t < 0) {
            val sp = PreferenceManager.getDefaultSharedPreferences(context)
            val hour = Integer.parseInt(sp.getString(
                context.resources.getString(R.string.pref_key_auto_update_hours),
                "$DEFAULT_UPDATE_HOURS"))
            t = AlarmManager.INTERVAL_HOUR * hour
        }

        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutoUpdateAlarmReceiver::class.java)
        intent.putExtra(ONE_TIME, java.lang.Boolean.FALSE)
        val pi = PendingIntent.getBroadcast(context, 0, intent, 0)
        //After after t seconds
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, t, pi)
        Log.i(TAG, "setAlarm:$t")
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AutoUpdateAlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
        Log.i(TAG, "cancelAlarm")
    }

    fun setOnetimeTimer(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AutoUpdateAlarmReceiver::class.java)
        intent.putExtra(ONE_TIME, java.lang.Boolean.TRUE)
        val pi = PendingIntent.getBroadcast(context, 0, intent, 0)
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi)
    }

    companion object {
        const val ONE_TIME = "onetime"
        const val AUTO_UPDATE_SERVICE = "AUTO_UPDATE_SERVICE"
        const val DEFAULT_UPDATE_HOURS = 6
        const val TAG = "AutoUpdateAlarm"
    }

}
