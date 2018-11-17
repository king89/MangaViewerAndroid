package com.king.mangaviewer.ui.main

import android.support.design.widget.FloatingActionButton

interface HasFloatActionButton {
    fun initFab(fab: FloatingActionButton)
    fun onClick()
}