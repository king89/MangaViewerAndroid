package com.king.mangaviewer.util.glide

import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

class CropImageTransformation : BitmapTransformation() {

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int,
            outHeight: Int): Bitmap {
        return if (toTransform.width > toTransform.height) {
            TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        } else {
            toTransform
        }
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("crop transformation".toByteArray())
    }
}