package com.king.mangaviewer.domain.repository

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.king.mangaviewer.R
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.util.GsonHelper
import java.util.ArrayList
import javax.inject.Inject

interface SettingRepository {
    fun getMangaProviderList(): List<MangaWebSource>
}

class SettingRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingRepository {

    private val _mangaProviderList: List<MangaWebSource> by lazy {
        val json = context.resources.openRawResource(R.raw.manga_source)
            .bufferedReader()
            .use { it.readText() }
        val listType = object : TypeToken<ArrayList<MangaWebSource>>() {}.type
        var list = GsonHelper.fromJson<List<MangaWebSource>>(json, listType)
        list = list.sorted().filter { it.enable > 0 }
        list
    }

    override fun getMangaProviderList(): List<MangaWebSource> {
        return _mangaProviderList
    }
}