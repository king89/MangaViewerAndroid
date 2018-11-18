package com.king.mangaviewer.util

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

object VersionUtil {
    fun isGreaterOrEqualApi19(): Boolean {
        return VERSION.SDK_INT >= VERSION_CODES.KITKAT
    }

    fun isGreaterOrEqualApi21(): Boolean {
        return VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP
    }
}