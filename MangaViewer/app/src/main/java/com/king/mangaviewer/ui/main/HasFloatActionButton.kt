package com.king.mangaviewer.ui.main

import com.google.android.material.floatingactionbutton.FloatingActionButton

interface HasFloatActionButton {
    fun initFab(fab: FloatingActionButton)
    fun onClick()
}