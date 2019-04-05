package com.king.mangaviewer.util.imageloader

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.king.mangaviewer.component.PageView
import com.king.mangaviewer.component.ReadingDirection
import com.king.mangaviewer.component.ReadingDirection.LTR
import com.king.mangaviewer.component.ReadingDirection.RTL
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.Logger
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.lang.Exception
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.math.max

class GlideImageLoader(private val context: Context) : ImageLoader {

    private var target: SimpleTarget<Bitmap>? = null

    override fun clear() {
        target?.run {
            GlideApp.with(context).clear(target)
        }
    }

    private val disposable = CompositeDisposable()

    override fun loadImage(imageView: SubsamplingScaleImageView, mangaUri: MangaUri,
        readingDirection: ReadingDirection,
        onProcessing: () -> Unit,
        onSuccess: (resource: Bitmap) -> Unit,
        onError: (e: Throwable) -> Unit) {

        target = object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap,
                transition: Transition<in Bitmap>?) {
                try {
                    onSuccess(resource)
                } catch (e: OutOfMemoryError) {
                    onError(e)
                }
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                onError(Exception("OnLoadFailed"))
            }
        }

        val webImageUrl = mangaUri.imageUri
        val UserAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5"
        val builder = LazyHeaders.Builder()
        builder.addHeader("Referer", mangaUri.referUri)
        builder.addHeader("User-Agent", UserAgent)
        val glideUrl = GlideUrl(webImageUrl, builder.build())
        Logger.d(PageView.TAG,
            "Download Image Url: " + "\n Referrer Url: " + mangaUri.referUri)

        Observable.fromCallable {
            onProcessing()
            GlideApp.with(context)
                .asBitmap()
                .load(glideUrl)
//                    .override(MAX_WIDTH, MAX_HEIGHT)
                .downsample(DownsampleStrategy.AT_MOST)
                .into(target!!)
        }.delay(500, MILLISECONDS)
            .subscribe()
            .apply { disposable.add(this) }
    }
}