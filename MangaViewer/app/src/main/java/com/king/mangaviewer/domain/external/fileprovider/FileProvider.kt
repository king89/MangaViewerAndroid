package com.king.mangaviewer.domain.external.fileprovider

import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.InputStream

interface FileProvider {

    fun saveFile(folderPath: String, fileName: String, data: InputStream): Single<String>

    fun zipFolder(inputFolderPath: String, outputFile: String): Completable

    fun moveFile(pathSrc: String, pathDes: String): Completable

    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory)
            for (child in fileOrDirectory.listFiles()!!)
                deleteRecursive(child)

        fileOrDirectory.delete()
    }

    fun getOutputFileName(folder: String): String
    fun getMenuDownloadPath(menu: MangaMenuItem): String
    fun getChapterDownloadPath(chapter: MangaChapterItem): String

}

