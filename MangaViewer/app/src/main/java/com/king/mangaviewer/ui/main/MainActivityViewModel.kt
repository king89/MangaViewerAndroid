package com.king.mangaviewer.ui.main

import com.king.mangaviewer.base.BaseActivityViewModel
import com.king.mangaviewer.domain.data.AppRepository
import com.king.mangaviewer.domain.data.MangaRepository
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
        private val AppRepository: AppRepository,
        private val MangaRepository: MangaRepository
) :
        BaseActivityViewModel() {

}
