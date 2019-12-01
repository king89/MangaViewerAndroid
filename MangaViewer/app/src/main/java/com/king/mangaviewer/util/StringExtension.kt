package com.king.mangaviewer.util

import com.king.mangaviewer.common.Constants.DATE_FORMAT_LONG
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatterBuilder
import java.net.MalformedURLException
import java.net.URL

fun String.toDateTime(): DateTime {
    return try {
        DateTime.parse(this,
            DateTimeFormatterBuilder().appendPattern(DATE_FORMAT_LONG).toFormatter())
    } catch (e: Exception) {
        Logger.e(TAG, e)
        DateTime.now()
    }
}

fun String.getFileExtension(): String {
    try {
        val url = URL(this)
        val file = url.file
        if (file.contains(".")) {

            val sub = file.substring(file.lastIndexOf('.') + 1)
            if (sub.isEmpty()) {
                return ""
            }

            return if (sub.contains("?")) {
                sub.substring(0, sub.indexOf('?'))
            } else sub
        }
    } catch (e: MalformedURLException) {
        return this
    }
    return this
}

const val TAG = "StringExtension"