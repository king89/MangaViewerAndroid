package com.king.mangaviewer.domain.repository

import com.king.mangaviewer.domain.data.local.DownloadState.DOWNLOADING
import com.king.mangaviewer.domain.data.local.DownloadState.ERROR
import com.king.mangaviewer.domain.data.local.DownloadState.FINISHED
import com.king.mangaviewer.domain.data.local.DownloadState.PENDING
import com.king.mangaviewer.domain.data.local.DownloadTask
import com.king.mangaviewer.domain.data.local.DownloadTaskStateItem
import com.king.mangaviewer.util.Logger
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.LinkedBlockingQueue
import javax.inject.Inject

interface DownloadTaskRepository {
    fun getTaskListObservable(): Observable<Map<String, DownloadTaskStateItem>>
    fun addTask(task: DownloadTask): Completable
    fun popTask(): Maybe<DownloadTask>
    fun finishTask(task: DownloadTask): Completable
    fun finishTaskWithError(task: DownloadTask): Completable
}

class DownloadTaskRepositoryImpl @Inject constructor(
) : DownloadTaskRepository {


    private var downloadTaskQueue = LinkedBlockingQueue<DownloadTask>()
    private var downloadTaskStateMap = mutableMapOf<String, DownloadTaskStateItem>()
    private var downloadTaskStateMapSubject = ReplaySubject.create<Map<String, DownloadTaskStateItem>>()

    override fun getTaskListObservable(): Observable<Map<String, DownloadTaskStateItem>> {
        return downloadTaskStateMapSubject
    }

    @Synchronized
    override fun addTask(task: DownloadTask): Completable {
        return Completable.fromAction {
            downloadTaskQueue.put(task)
            downloadTaskStateMap[task.chapter.hash] = DownloadTaskStateItem(task.chapter.menu.hash,
                task.chapter.hash, PENDING)
            downloadTaskStateMapSubject.onNext(downloadTaskStateMap)
            Logger.d(TAG, "addTask Download state updated: $downloadTaskStateMap")
        }
    }

    @Synchronized
    override fun popTask(): Maybe<DownloadTask> {
        //TODO handle pause task
        return if (downloadTaskQueue.isNotEmpty()) {
            val task = downloadTaskQueue.take()
            downloadTaskStateMap[task.chapter.hash]?.downLoadState = DOWNLOADING
            downloadTaskStateMapSubject.onNext(downloadTaskStateMap)
            Logger.d(TAG, "popTask Download state updated: $downloadTaskStateMap")
            Maybe.just(task)
        } else {
            Maybe.empty()
        }
    }

    @Synchronized
    override fun finishTask(task: DownloadTask): Completable {
        return Completable.fromAction {
            downloadTaskStateMap[task.chapter.hash]?.downLoadState = FINISHED
            downloadTaskStateMapSubject.onNext(downloadTaskStateMap)
            Logger.d(TAG, "finishTask Download state updated: $downloadTaskStateMap")
        }
    }

    @Synchronized
    override fun finishTaskWithError(task: DownloadTask): Completable {
        return Completable.fromAction {
            downloadTaskStateMap[task.chapter.hash]?.downLoadState = ERROR
            downloadTaskStateMapSubject.onNext(downloadTaskStateMap)
            Logger.d(TAG, "finishTaskWithError Download state updated: $downloadTaskStateMap")
        }
    }

    companion object {
        const val TAG = "DownloadTaskRepositoryImpl"
    }
}