package com.king.mangaviewer.util

import android.util.Log

object Logger {
    @JvmStatic
    @JvmOverloads
    fun d(tag: String, message: String, e: Throwable? = null) {
        try {
            Log.d(tag, message, e)
        } catch (ex: Throwable) {
            println("$tag $message ${e}")
        }
    }

    @JvmStatic
    @JvmOverloads
    fun i(tag: String, message: String, e: Throwable? = null) {

    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String, e: Throwable? = null, message: String = "") {
        try {
            Log.e(tag, message, e)
            e?.printStackTrace()
        } catch (ex: Throwable) {
            println("$tag $message ${e}")
        }
    }
}