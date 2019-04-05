package com.king.mangaviewer.util.imageloader

import android.graphics.Bitmap
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.king.mangaviewer.component.ReadingDirection
import com.king.mangaviewer.model.MangaUri

interface ImageLoader {
    fun loadImage(imageView: SubsamplingScaleImageView, mangaUri: MangaUri,
        readingDirection: ReadingDirection,
        onProcessing: () -> Unit,
        onSuccess: (resource: Bitmap) -> Unit,
        onError: (e: Throwable) -> Unit)

    fun clear()
}

