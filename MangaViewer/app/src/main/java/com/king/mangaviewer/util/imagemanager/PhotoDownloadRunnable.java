package com.king.mangaviewer.util.imagemanager;

import android.graphics.drawable.Drawable;
import android.util.Log;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.util.MangaHelperV2;
import com.king.mangaviewer.util.NetworkHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;

/**
 * Created by KinG on 3/8/2016.
 */
@Deprecated
public class PhotoDownloadRunnable implements Runnable {

    // Sets a tag for this class
    @SuppressWarnings("unused")
    private static final String LOG_TAG = "PhotoDownloadRunnable";

    // Constants for indicating the state of the download
    static final int HTTP_STATE_FAILED = -1;
    static final int HTTP_STATE_STARTED = 0;
    static final int HTTP_STATE_COMPLETED = 1;

    // Defines a field that contains the calling object of type PhotoTask.
    final TaskRunnableDownloadMethods mPhotoTask;

    /**
     * This constructor creates an instance of PhotoDownloadRunnable and stores in it a reference
     * to the PhotoTask instance that instantiated it.
     *
     * @param photoTask The PhotoTask, which implements TaskRunnableDecodeMethods
     */
    PhotoDownloadRunnable(TaskRunnableDownloadMethods photoTask) {
        mPhotoTask = photoTask;
    }

    /**
     * Starts executing the active part of the class' code. This method is
     * called when a thread is started that has been created with a class which
     * implements {@code Runnable}.
     */
    @Override
    public void run() {
         /*
         * Stores the current Thread in the the PhotoTask instance, so that the instance
         * can interrupt the Thread.
         */
        mPhotoTask.setDownloadThread(Thread.currentThread());

        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        Drawable byteBuffer = mPhotoTask.getDrawable();
        InputStream i = null;
        try {
            if (Thread.interrupted()) {
                Log.i("PhotoDownloadRunnable", "Thread interrupted");
                //throw new InterruptedException();
            }
            try {

                String url = MangaHelperV2.INSTANCE.getMenuCover(mPhotoTask.getMangaMenuItem());

                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                i = NetworkHelper.downLoadFromUrl(url,mPhotoTask.getMangaMenuItem().getUrl());
                byteBuffer = Drawable.createFromStream(i, "src");
                if (null == byteBuffer) {
                    throw new IOException();
                }
            /*
             * Stores the downloaded bytes in the byte buffer in the PhotoTask instance.
             */
                mPhotoTask.setDrawable(byteBuffer);

            /*
             * Sets the status message in the PhotoTask instance. This sets the
             * ImageView background to indicate that the image is being
             * decoded.
             */
                mPhotoTask.handleDownloadState(HTTP_STATE_COMPLETED);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e) {
                Log.i("PhotoDownloadRunnable", "Thread interrupted");
            } catch (InterruptedIOException e) {
                Log.i("PhotoDownloadRunnable", "InterruptedIOException");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {

            // If the byteBuffer is null, reports that the download failed.
            if (null == byteBuffer) {
                mPhotoTask.handleDownloadState(HTTP_STATE_FAILED);
            }

            /*
             * The implementation of setHTTPDownloadThread() in PhotoTask calls
             * PhotoTask.setCurrentThread(), which then locks on the static ThreadPool
             * object and returns the current thread. Locking keeps all references to Thread
             * objects the same until the reference to the current Thread is deleted.
             */

            // Sets the reference to the current Thread to null, releasing its storage
            mPhotoTask.setDownloadThread(null);

            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }

    }

    /**
     * An interface that defines methods that PhotoTask implements. An instance of
     * PhotoTask passes itself to an PhotoDownloadRunnable instance through the
     * PhotoDownloadRunnable constructor, after which the two instances can access each other's
     * variables.
     */
    interface TaskRunnableDownloadMethods {

        /**
         * Sets the Thread that this instance is running on
         *
         * @param currentThread the current Thread
         */
        void setDownloadThread(Thread currentThread);

        /**
         * Returns the current contents of the download buffer
         *
         * @return The byte array downloaded from the URL in the last read
         */
        Drawable getDrawable();

        /**
         * Sets the current contents of the download buffer
         *
         * @param d The bytes that were just read
         */
        void setDrawable(Drawable d);

        /**
         * Defines the actions for each state of the PhotoTask instance.
         *
         * @param state The current state of the task
         */
        void handleDownloadState(int state);


        MangaMenuItem getMangaMenuItem();
    }

}
