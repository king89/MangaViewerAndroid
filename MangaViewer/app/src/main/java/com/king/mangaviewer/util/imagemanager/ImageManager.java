package com.king.mangaviewer.util.imagemanager;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.model.MangaMenuItem;

import java.net.URL;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by KinG on 3/8/2016.
 */
public class ImageManager {
    /*
    * Status indicators
    */
    static final int DOWNLOAD_FAILED = -1;
    static final int DOWNLOAD_STARTED = 1;
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    static final int TASK_COMPLETE = 4;

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;

    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT;

    // Sets the initial threadpool size to 8
    private static final int CORE_POOL_SIZE = 8;

    // Sets the maximum threadpool size to 8
    private static final int MAXIMUM_POOL_SIZE = 8;
    private static final int IMAGE_CACHE_SIZE = 100;

    /*
   * Creates a cache of byte arrays indexed by image URLs. As new items are added to the
   * cache, the oldest items are ejected and subject to garbage collection.
   */
    private final LruCache<String, Drawable> mPhotoCache;

    // A queue of Runnables for the image download pool
    private final BlockingQueue<Runnable> mDownloadWorkQueue;

    // A queue of PhotoManager tasks. Tasks are handed to a ThreadPool.
    private final Queue<PhotoTask> mPhotoTaskWorkQueue;

    // A managed pool of background download threads
    private final ThreadPoolExecutor mDownloadThreadPool;

    // An object that manages Messages in a Thread
    private Handler mHandler;

    // A single instance of PhotoManager, used to implement the singleton pattern
    private static ImageManager sInstance = null;

    // A static block that sets class fields
    static {

        // The time unit for "keep alive" is in seconds
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        // Creates a single static instance of PhotoManager
        sInstance = new ImageManager();
    }

    /**
     * Returns the PhotoManager object
     * @return The global PhotoManager object
     */
    public static ImageManager getInstance() {
        return sInstance;
    }

    private ImageManager() {
        mDownloadWorkQueue = new LinkedBlockingQueue<>();;
        mPhotoTaskWorkQueue = new LinkedBlockingQueue<>();;
        mDownloadThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDownloadWorkQueue);
        mPhotoCache = new LruCache<>(IMAGE_CACHE_SIZE);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message inputMessage) {
                // Gets the image task from the incoming Message object.
                PhotoTask photoTask = (PhotoTask) inputMessage.obj;

                // Sets an PhotoView that's a weak reference to the
                // input ImageView
                MyImageView localView = photoTask.getPhotoView();

                if (localView != null){
/*
                     * Gets the URL of the *weak reference* to the input
                     * ImageView. The weak reference won't have changed, even if
                     * the input ImageView has.
                     */
                    URL localURL = localView.getLocation();

                    /*
                     * Compares the URL of the input ImageView to the URL of the
                     * weak reference. Only updates the bitmap in the ImageView
                     * if this particular Thread is supposed to be serving the
                     * ImageView.
                     */
                    if (photoTask.getImageURL() == localURL)

                        /*
                         * Chooses the action to take, based on the incoming message
                         */
                        switch (inputMessage.what) {

                            // If the download has started, sets background color to dark green
                            case DOWNLOAD_STARTED:
                                //localView.setStatusResource(R.drawable.imagedownloading);
                                break;

                            /*
                             * If the download is complete, but the decode is waiting, sets the
                             * background color to golden yellow
                             */
                            case DOWNLOAD_COMPLETE:
                                // Sets background color to golden yellow
                                //localView.setStatusResource(R.drawable.decodequeued);
                                localView.setImageDrawable(photoTask.getDrawable());
                                break;
                            // If the decode has started, sets background color to orange
                            case DECODE_STARTED:
                                //localView.setStatusResource(R.drawable.decodedecoding);
                                break;
                            /*
                             * The decoding is done, so this sets the
                             * ImageView's bitmap to the bitmap in the
                             * incoming message
                             */
                            case TASK_COMPLETE:
//                                localView.setImageBitmap(photoTask.getImage());
//                                recycleTask(photoTask);
                                break;
                            // The download failed, sets the background color to dark red
                            case DOWNLOAD_FAILED:
//                                localView.setStatusResource(R.drawable.imagedownloadfailed);
//
//                                // Attempts to re-use the Task object
//                                recycleTask(photoTask);
                                break;
                            default:
                                // Otherwise, calls the super method
                                super.handleMessage(inputMessage);
                        }
                }
            }
        };

    }


    public static void removeDownload(PhotoTask downloaderTask, MangaMenuItem menu) {
        // If the Thread object still exists and the download matches the specified URL
        if (downloaderTask != null && downloaderTask.getMangaMenuItem().getHash().equals(menu.getHash())) {

            /*
             * Locks on this class to ensure that other processes aren't mutating Threads.
             */
            synchronized (sInstance) {

                // Gets the Thread that the downloader task is running on
                Thread thread = downloaderTask.getCurrentThread();

                // If the Thread exists, posts an interrupt to it
                if (null != thread)
                    thread.interrupt();
            }
            /*
             * Removes the download Runnable from the ThreadPool. This opens a Thread in the
             * ThreadPool's work queue, allowing a task in the queue to start.
             */
            sInstance.mDownloadThreadPool.remove(downloaderTask.getHTTPDownloadRunnable());
        }
    }

    public static PhotoTask startDownload(MyImageView imageView, boolean cacheFlag) {
        /*
         * Gets a task from the pool of tasks, returning null if the pool is empty
         */
        PhotoTask downloadTask = sInstance.mPhotoTaskWorkQueue.poll();

        // If the queue was empty, create a new task instead.
        if (null == downloadTask) {
            downloadTask = new PhotoTask();
        }

        // Initializes the task
        downloadTask.initializeDownloaderTask(ImageManager.sInstance, imageView, cacheFlag);
        downloadTask.mMangaMenuItem = imageView.mMangaMenuItem;
        /*
         * Provides the download task with the cache buffer corresponding to the URL to be
         * downloaded.
         */
        downloadTask.setDrawable(sInstance.mPhotoCache.get(downloadTask.mMangaMenuItem.getHash()));

        // If the byte buffer was empty, the image wasn't cached
        if (null == downloadTask.getDrawable()) {

            /*
             * "Executes" the tasks' download Runnable in order to download the image. If no
             * Threads are available in the thread pool, the Runnable waits in the queue.
             */
            sInstance.mDownloadThreadPool.execute(downloadTask.getHTTPDownloadRunnable());

            // Sets the display to show that the image is queued for downloading and decoding.
            //imageView.setStatusResource(R.drawable.imagequeued);

            // The image was cached, so no download is required.
        } else {

            /*
             * Signals that the download is "complete", because the byte array already contains the
             * undecoded image. The decoding starts.
             */

            sInstance.handleState(downloadTask, DOWNLOAD_COMPLETE);
        }

        // Returns a task object, either newly-created or one from the task pool
        return downloadTask;
    }

    public void handleState(PhotoTask photoTask, int state) {
        switch (state) {
            // The task finished downloading and decoding the image
            case DOWNLOAD_COMPLETE:

                // Puts the image into cache
                if (photoTask.isCacheEnabled()) {
                    // If the task is set to cache the results, put the buffer
                    // that was
                    // successfully decoded into the cache
                    mPhotoCache.put(photoTask.mMangaMenuItem.getHash(), photoTask.getDrawable());
                }

                // Gets a Message object, stores the state in it, and sends it to the Handler
                Message completeMessage = mHandler.obtainMessage(state, photoTask);
                completeMessage.sendToTarget();
                break;

            default:
                mHandler.obtainMessage(state, photoTask).sendToTarget();
                break;
        }
    }
}
