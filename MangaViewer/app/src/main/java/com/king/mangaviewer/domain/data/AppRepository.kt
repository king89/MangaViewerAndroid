package com.king.mangaviewer.domain.data

import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.viewmodel.AppViewModel
import javax.inject.Inject

interface AppRepository {
    val appViewModel: AppViewModel
}

class AppRepositoryImpl @Inject constructor(override val appViewModel: AppViewModel) :
        AppRepository {
    init {
        Logger.d("-=-=", "create AppRepositoryImpl ")

    }
}