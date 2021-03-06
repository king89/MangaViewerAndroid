package com.king.mangaviewer.component;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.util.imagemanager.ImageManager;
import com.king.mangaviewer.util.imagemanager.PhotoTask;

import java.net.URL;

/**
 * Created by KinG on 3/8/2016.
 */
public class MyImageView extends ImageView {
    private boolean mCacheEnabled;
    // The URL that points to the source of the image for this ImageView
    private URL mImageURL;
    private MangaMenuItem mMangaMenuItem;
    // The Thread that will be used to download the image for this ImageView
    private PhotoTask mDownloadThread;
    private boolean mCacheFlag;

    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Detects the state of caching
    boolean isCacheEnabled() {
        return mCacheEnabled;
    }

    /**
     * Attempts to set the picture URL for this ImageView and then download the picture.
     * <p/>
     * If the picture URL for this view is already set, and the input URL is not the same as the
     * stored URL, then the picture has moved and any existing downloads are stopped.
     * <p/>
     * If the input URL is the same as the stored URL, then nothing needs to be done.
     * <p/>
     * If the stored URL is null, then this method starts a download and decode of the picture
     *
     * @param menu    An incoming URL for a Picasa picture
     * @param cacheFlag     Whether to use caching when doing downloading and decoding
     * @param imageDrawable The Drawable to use for this ImageView
     */
    public void setImageURL(MangaMenuItem menu, boolean cacheFlag, Drawable imageDrawable) {
        // If the picture URL for this ImageView is already set
        if (mMangaMenuItem != null) {

            // If the stored URL doesn't match the incoming URL, then the picture has changed.
            if (!mMangaMenuItem.getHash().equals(menu.getHash())) {

                // Stops any ongoing downloads for this ImageView
                ImageManager.removeDownload(mDownloadThread, menu);
            } else {

                // The stored URL matches the incoming URL. Returns without doing any work.
                return;
            }
        }

        // Sets the Drawable for this ImageView
        setImageDrawable(imageDrawable);

        mMangaMenuItem = menu;
        // If the draw operation for this ImageVIew has completed, and the picture URL isn't empty
        if (menu != null) {

            // Sets the cache flag
            mCacheFlag = cacheFlag;

            /*
             * Starts a download of the picture file. Notice that if caching is on, the picture
             * file's contents may be taken from the cache.
             */
            mDownloadThread = ImageManager.startDownload(this, cacheFlag);
        }
    }


    public MangaMenuItem getMangaMenuItem() {
        return mMangaMenuItem;
    }
}
