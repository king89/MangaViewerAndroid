package com.king.mangaviewer.service.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.Request.NETWORK_WIFI
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import com.king.mangaviewer.domain.data.local.DownloadTask
import com.king.mangaviewer.domain.external.fileprovider.DownloadFileProvider
import com.king.mangaviewer.domain.repository.DownloadTaskRepository
import com.king.mangaviewer.domain.usecase.FinishDownloadUseCase
import com.king.mangaviewer.domain.usecase.GetPageListUseCase
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.concat
import com.king.mangaviewer.util.getFileExtension
import dagger.android.AndroidInjection
import java.io.File
import javax.inject.Inject

internal data class Task(val uri: String, val refer: String, val fileUri: Uri, val fileName: String)

class MangaDownloadService : IntentService("MangaDownloadService") {

    @Inject
    lateinit var downloadTaskRepository: DownloadTaskRepository

    @Inject
    lateinit var getPageListUseCase: GetPageListUseCase

    @Inject
    lateinit var downloadFileProvider: DownloadFileProvider

    @Inject
    lateinit var finishDownloadUseCase: FinishDownloadUseCase

    private val downloadManager by lazy {
        getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onHandleIntent(p0: Intent?) {
        val task = downloadTaskRepository.popTask().blockingGet()
        task?.let {
            Logger.d(TAG, "get download task: $task")
            startDownloadTask(it)
        }
    }

    private fun startDownloadTask(task: DownloadTask) {
        try {
            val chapter = task.chapter
            val chapterFolderName = getChapterFolderName(chapter)
            val menuFolderName = getMenuFolderName(chapter)

            val taskList = parseTask(chapter, chapterFolderName)
            Logger.d(TAG, "start download list: ${taskList.map { it.uri }}")

            val jobIdList = mutableListOf<Long>()

            deleteFolder(chapterFolderName)

            taskList.forEach { item ->
                val request = DownloadManager.Request(Uri.parse(item.uri)).apply {
                    addRequestHeader("Referer", item.refer)
                    setAllowedNetworkTypes(NETWORK_WIFI)
                    setNotificationVisibility(VISIBILITY_VISIBLE)
                    setDestinationUri(item.fileUri)
                    setTitle("${task.chapter.title} - ${item.fileName}")
                    setDescription("${task.chapter.title} - ${item.fileName}")
                }

                downloadManager.enqueue(request).also { jobIdList.add(it) }
            }

            waitForAllDownloaded(task, menuFolderName, chapterFolderName, jobIdList)

            deleteFolder(chapterFolderName)
        } catch (e: IllegalArgumentException) {
            Logger.e(TAG, e, "cannot download task: ${task.chapter.url}")
            downloadTaskRepository.finishTaskWithError(task).subscribe()
        }
        startNextTask(this)
    }

    private fun getChapterFolderName(chapter: MangaChapterItem): String {
        val baseFolder = Environment.getExternalStoragePublicDirectory("")
        return File(baseFolder, DownloadFileProvider.FOLDER_NAME)
            .concat(chapter.mangaWebSource.displayName)
            .concat(chapter.menu.hash)
            .concat(chapter.hash)
            .absolutePath
    }

    private fun getMenuFolderName(chapter: MangaChapterItem): String {
        val baseFolder = Environment.getExternalStoragePublicDirectory("")
        return File(baseFolder, DownloadFileProvider.FOLDER_NAME)
            .concat(chapter.mangaWebSource.displayName)
            .concat(chapter.menu.hash)
            .absolutePath
    }


    private fun deleteFolder(folder: String) {
        val file = File(folder)
        downloadFileProvider.deleteRecursive(file)
        Logger.d(TAG, "delete dir: ${file.absolutePath},$folder")
    }

    private fun waitForAllDownloaded(downloadTask: DownloadTask, menuFolder: String,
        chapterFolder: String,
        jobIdList: MutableList<Long>) {
        while (true) {
            if (checkAllSuccess(jobIdList)) {
                val outputFile = downloadFileProvider.getOutputFileName(chapterFolder)
                downloadFileProvider.zipFolder(chapterFolder, outputFile).subscribe()
                finishDownloadUseCase.execute(downloadTask, menuFolder, outputFile).subscribe()
                Logger.d(TAG, "All task finished")

                break
            } else {
                Thread.sleep(1000)
            }
        }

    }

    @SuppressLint("Range")
    private fun checkAllSuccess(jobIdList: MutableList<Long>): Boolean {
        var result = true
        val cursor: Cursor? = downloadManager.query(
            DownloadManager.Query().setFilterById(*jobIdList.toLongArray()))
        while (cursor?.moveToNext() == true) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (status == DownloadManager.STATUS_FAILED) {
                // do something when failed
                result = false
                break
            } else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                // do something pending or paused
                result = false
                break
            } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                // do something when successful
            } else if (status == DownloadManager.STATUS_RUNNING) {
                // do something when running
                result = false
                break
            }
        }
        cursor?.close()

        return result
    }

    private fun parseTask(chapter: MangaChapterItem, folderName: String): List<Task> {
        val result = mutableListOf<Task>()
        getPageListUseCase.execute(chapter)
            .blockingGet()
            .forEachIndexed { index, item ->
                val extName = item.url.getFileExtension()
                val fileName = String.format("%03d", index).let { "$it.$extName" }
                val file = File(folderName, fileName)
                result.add(Task(item.url, item.referUrl, Uri.fromFile(file), fileName))
            }
        return result

    }


    companion object {
        const val TAG = "MangaDownloadService"
        fun startNextTask(context: Context) {
            val intent = Intent(context, MangaDownloadService::class.java)
            context.startService(intent)
        }
    }
}