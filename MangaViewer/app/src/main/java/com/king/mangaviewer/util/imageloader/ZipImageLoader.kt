package com.king.mangaviewer.util.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.king.mangaviewer.component.ReadingDirection
import com.king.mangaviewer.model.MangaUri
import java.io.IOException
import java.lang.Exception
import java.util.zip.ZipFile

class ZipImageLoader : ImageLoader {
    override fun loadImage(imageView: SubsamplingScaleImageView,
        mangaUri: MangaUri,
        readingDirection: ReadingDirection,
        onProcessing: () -> Unit,
        onSuccess: (resource: Bitmap) -> Unit,
        onError: (e: Throwable) -> Unit) {
        val bitmap: Bitmap
        //load zip image
        val zf: ZipFile
        try {
            zf = ZipFile(mangaUri.referUri)
            val ze = zf.getEntry(mangaUri.imageUri)
            bitmap = BitmapFactory.decodeStream(zf.getInputStream(ze))
            //show image
            onSuccess(bitmap)
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun clear() {
    }

}