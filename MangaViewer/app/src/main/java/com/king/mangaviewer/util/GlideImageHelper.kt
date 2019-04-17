package com.king.mangaviewer.util

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import android.widget.ImageView.ScaleType.CENTER
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.Companion
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.glide.CropImageTransformation
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.HashMap

/**
 * Created by king on 2017-07-23.
 */

object GlideImageHelper {
    fun getImageWithHeader(imageView: ImageView?, url: String, headers: HashMap<String, String>?) {
        val headerBuilder = LazyHeaders.Builder()
        if (headers != null) {
            for (s in headers.keys) {
                headerBuilder.addHeader(s, headers[s])
            }
        }
        if (TextUtils.isEmpty(url)) {
            return
        }
        val glideUrl = GlideUrl(url, headerBuilder.build())
        if (imageView != null)
            GlideApp.with(imageView)
                    .load(glideUrl)
                    .placeholder(R.mipmap.ic_preloader_background)

    }

    fun getMenuCover(imageView: ImageView, item: MangaMenuItem,
            vararg transformation: BitmapTransformation?): Completable {
        return Single.fromCallable {
            val url = MangaHelperV2.getMenuCover(item)
            if (url.isEmpty()) return@fromCallable Any()
            val header = LazyHeaders.Builder().addHeader("Referer", item.url).build()
            GlideUrl(url, header)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    (it as? GlideUrl) ?: apply {
                        imageView.setImageResource(R.drawable.ic_manga_cover_placeholder)
                        imageView.scaleType = CENTER
                        return@doOnSuccess
                    }
                    val call = GlideApp.with(imageView)
                            .load(it)
                            .placeholder(R.color.manga_place_holder)
                    if (transformation.isNotEmpty()) {
                        transformation.forEach {
                            if (it != null) {
                                call.transform(it)
                            }
                        }
                    }
                    call.into(imageView)
                }
                .doOnError { Logger.e(TAG, it) }
                .ignoreElement()
                .onErrorComplete()

    }

    const val TAG = "GlideImageHelper"
}
