package com.king.mangaviewer.util

import android.app.Application
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
    fun e(tag: String, message: String, e: Throwable? = null) {
        try {
            Log.e(tag, message, e)
        } catch (ex: Throwable) {
            println("$tag $message ${e}")
        }
    }
}