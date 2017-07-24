package com.king.mangaviewer.util;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaderFactory;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.king.mangaviewer.R;

import java.util.HashMap;

/**
 * Created by king on 2017-07-23.
 */

public class GlideImageHelper {
    public static void getImageWithHeader(ImageView imageView, String url, HashMap<String, String> headers) {
        LazyHeaders.Builder headerBuilder = new LazyHeaders.Builder();
        if (headers != null) {
            for (String s : headers.keySet()) {
                headerBuilder.addHeader(s, headers.get(s));
            }
        }
        if (TextUtils.isEmpty(url)){
            return;
        }
        GlideUrl glideUrl = new GlideUrl(url, headerBuilder.build());
        if (imageView != null)
            Glide.with(imageView.getContext())
                    .load(glideUrl)
                    .placeholder(R.mipmap.ic_preloader_background)
                    .into(imageView);
    }
}
