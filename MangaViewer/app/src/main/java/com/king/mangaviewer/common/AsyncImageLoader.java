package com.king.mangaviewer.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class AsyncImageLoader {
    //SoftReference是软引用，是为了更好的为了系统回收变量
    private HashMap<String, SoftReference<Drawable>> imageCache;

    public AsyncImageLoader() {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
    }

    public static Drawable loadImageFromUrl(String url) {
        URL m;
        InputStream i = null;
        try {
            m = new URL(url);
            i = (InputStream) m.getContent();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = Drawable.createFromStream(i, "src");
        return d;
    }

    public Drawable loadDrawable(final String imageUrl, final ImageView imageView, final ImageCallback imageCallback) {
        if (imageCache.containsKey(imageUrl)) {
            //从缓存中获取
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            Drawable drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageView, imageUrl);
            }
        };
        //建立新一个新的线程下载图片
        new Thread() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(imageUrl);
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public Drawable loadImageFromMenuItem(final Context context, final MangaMenuItem menu, final ImageView imageView, final ImageCallback imageCallback) {

        if (!menu.getImagePath().isEmpty() && imageCache.containsKey(menu.getImagePath())) {
            //从缓存中获取
            SoftReference<Drawable> softReference = imageCache.get(menu.getImagePath());
            Drawable drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        }

        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                ImageAndUrl iau = (ImageAndUrl) message.obj;
                imageCallback.imageLoaded(iau.drawable, imageView, iau.imageUrl);
            }
        };
        //建立新一个新的线程下载图片
        new Thread() {
            @Override
            public void run() {
                final String imageUrl = new MangaHelper(context).getMenuCover(menu);
                Drawable drawable = loadImageFromUrl(imageUrl);
                imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
                Message message = handler.obtainMessage(0, new ImageAndUrl(drawable, imageUrl));
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    private class ImageAndUrl {
        public Drawable drawable;
        public String imageUrl;

        public ImageAndUrl(Drawable drawable, String imageUrl) {
            this.drawable = drawable;
            this.imageUrl = imageUrl;
        }
    }

    //回调接口
    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl);
    }
}