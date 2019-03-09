package com.king.mangaviewer.util

import com.king.mangaviewer.common.Constants.DATE_FORMAT_LONG
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder

fun String.toDateTime(): DateTime {
    return try {
        DateTime.parse(this,
                DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_LONG).toFormatter())
    } catch (e: Exception) {
        Logger.e(TAG, e)
        DateTime.now()
    }
}

const val TAG = "StringExtension"