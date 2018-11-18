package com.king.mangaviewer.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.view.Window
import com.king.mangaviewer.R
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import javax.inject.Inject

class AppNavigator @Inject constructor(private val activity: Activity) {
    fun navigateToChapter(vararg sharedElementPair: Pair<View, String>) {
        val intent = Intent(activity, MangaChapterActivity::class.java)
        if (VersionUtil.isGreaterOrEqualApi21()) {
            val statusBar = activity.findViewById<View>(android.R.id.statusBarBackground)
            val navigationBar = activity.findViewById<View>(android.R.id.navigationBarBackground)
            val appBarLayout = activity.findViewById<View>(R.id.appBarLayout)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    *sharedElementPair,
                    Pair.create(navigationBar,
                            Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)).toBundle()
            activity.startActivity(intent, options)
        } else {
            activity.startActivity(intent)
            activity.overridePendingTransition(R.anim.in_rightleft,
                    R.anim.out_rightleft)
        }
    }
}