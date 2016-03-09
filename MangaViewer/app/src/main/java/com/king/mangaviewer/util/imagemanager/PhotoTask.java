package com.king.mangaviewer.util.imagemanager;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.king.mangaviewer.component.MyImageView;

import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * Created by KinG on 3/8/2016.
 */
public class PhotoTask implements PhotoDownloadRunnable.TaskRunnableDownloadMethods{

    /*
     * Fields containing references to the two runnable objects that handle downloading and
     * decoding of the image.
     */
    private Runnable mDownloadRunnable;

    // The Thread on which this task is currently running.
    private Thread mCurrentThread;

    /*
     * An object that contains the ThreadPool singleton.
     */
    private static ImageManager sPhotoManager;

    private WeakReference<MyImageView> mImageWeakRef;
    private URL mImageURL;
    private boolean mCacheEnabled;
    private Drawable mDrawable;

    /**
     * Creates an PhotoTask containing a download object and a decoder object.
     */
    PhotoTask() {
        // Create the runnables
        mDownloadRunnable = new PhotoDownloadRunnable(this);
        sPhotoManager = ImageManager.getInstance();
    }

    public MyImageView getPhotoView() {
        return mImageWeakRef.get();
    }

    public void initializeDownloaderTask(ImageManager photoManager, MyImageView imageView, boolean cacheFlag) {
        // Sets this object's ThreadPool field to be the input argument
        sPhotoManager = photoManager;

        // Gets the URL for the View
        mImageURL = imageView.getLocation();

        // Instantiates the weak reference to the incoming view
        mImageWeakRef = new WeakReference<MyImageView>(imageView);

        // Sets the cache flag to the input argument
        mCacheEnabled = cacheFlag;
    }

    @Override
    public void setDrawable(Drawable d){
        mDrawable = d;
    }

    /**
     * Defines the actions for each state of the PhotoTask instance.
     *
     * @param state The current state of the task
     */
    @Override
    public void handleDownloadState(int state) {
        int outState;

        // Converts the download state to the overall state
        switch(state) {
            case PhotoDownloadRunnable.HTTP_STATE_COMPLETED:
                outState = ImageManager.DOWNLOAD_COMPLETE;
                break;
            case PhotoDownloadRunnable.HTTP_STATE_FAILED:
                outState = ImageManager.DOWNLOAD_FAILED;
                break;
            default:
                outState = ImageManager.DOWNLOAD_STARTED;
                break;
        }
        // Passes the state to the ThreadPool object.
        handleState(outState);
    }

    // Delegates handling the current state of the task to the PhotoManager object
    void handleState(int state) {
        sPhotoManager.handleState(this, state);
    }


    /**
     * Sets the Thread that this instance is running on
     *
     * @param currentThread the current Thread
     */
    @Override
    public void setDownloadThread(Thread currentThread) {

    }
    @Override
    public Drawable getDrawable(){
        return mDrawable;
    }

    @Override
    public URL getImageURL() {
        return mImageURL;
    }

    public Runnable getHTTPDownloadRunnable() {
        return mDownloadRunnable;
    }

    public boolean isCacheEnabled() {
        return mCacheEnabled;
    }
}
