package com.king.mangaviewer.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.king.mangaviewer.model.MangaUri

import java.lang.reflect.Type

/**
 * Created by king on 2017-08-13.
 */

object GsonHelper {
    val gson = Gson()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> fromJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

}