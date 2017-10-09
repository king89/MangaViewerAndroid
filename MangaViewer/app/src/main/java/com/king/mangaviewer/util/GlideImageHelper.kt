package com.king.mangaviewer.util

import android.text.TextUtils
import android.widget.ImageView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaderFactory
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.king.mangaviewer.R

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
            Glide.with(imageView.context)
                    .load(glideUrl)
                    .placeholder(R.mipmap.ic_preloader_background)

    }
}
