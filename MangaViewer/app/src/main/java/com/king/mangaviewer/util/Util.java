package com.king.mangaviewer.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.king.mangaviewer.BuildConfig;

/**
 * Created by king on 2017-07-23.
 */

public class Util {
    public static String getVersionName(Context context) {
       return BuildConfig.VERSION_NAME;
    }
}
