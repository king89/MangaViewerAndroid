package com.king.mangaviewer.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.king.mangaviewer.R

class BrightnessBar @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null,
    defStyle: Int = 0) :
    LinearLayout(context, attributeSet, defStyle) {

    init {
        View.inflate(context, R.layout.view_brightness_bar, this)
    }

    fun setProgress(value: Int) {
        this.findViewById<ProgressBar>(R.id.pbBrightness).progress = value
    }

}