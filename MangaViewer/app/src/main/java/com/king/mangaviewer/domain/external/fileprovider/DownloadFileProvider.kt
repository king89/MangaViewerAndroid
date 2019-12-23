package com.king.mangaviewer.domain.external.fileprovider

import android.content.Context
import android.os.Environment
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.concat
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class DownloadFileProvider @Inject constructor(
    private val context: Context
) : FileProvider {

    private val externalFolder by lazy {
        File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            FOLDER_NAME)
    }

    override fun saveFile(folderPath: String, fileName: String, data: InputStream): Single<String> {
        return Single.fromCallable {
            val dir = File(externalFolder, folderPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(dir, fileName)
            file.createNewFile()
            if (file.exists() && file.canWrite()) {
                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    val buffer = ByteArray(4 * 1024)
                    var len = 0
                    while (true) {
                        len = data.read(buffer)
                        if (len == -1) break

                        fos.write(buffer, 0, len)
                    }
                } finally {
                    fos?.flush()
                    fos?.close()
                }
            }
            file.absolutePath
        }
    }

    override fun zipFolder(inputFolderPath: String, outputFile: String): Completable {
        return Completable.fromCallable {
            val zipFile = File(outputFile).also {
                if (!it.exists()) {
                    it.createNewFile()
                }
            }
            val fos = FileOutputStream(zipFile)
            val zos = ZipOutputStream(fos)
            val srcFile = File(inputFolderPath)
            val files = srcFile.listFiles()
            Logger.d(TAG, "start Zip directory: " + srcFile.name)
            for (i in files!!.indices) {
//                Logger.d(TAG, "Adding file: " + files[i].name)
                val buffer = ByteArray(1024)
                val fis = FileInputStream(files[i])
                zos.putNextEntry(ZipEntry(files[i].name))
                var length: Int
                while ((fis.read(buffer).also { length = it }) > 0) {
                    zos.write(buffer, 0, length)
                }
                zos.closeEntry()
                fis.close()
            }
            zos.close()
            Logger.d(TAG, "finished Zip directory: " + srcFile.name)

        }
    }


    override fun moveFile(pathSrc: String, pathDes: String): Completable {
        TODO(
            "not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOutputFileName(folder: String): String {
        return "$folder.zip"

    }

    override fun getMenuDownloadPath(menu: MangaMenuItem): String {
        return File(externalFolder, menu.hash).absolutePath
    }

    override fun getChapterDownloadPath(chapter: MangaChapterItem): String {
        return File(externalFolder, chapter.menu.hash).concat(chapter.hash).absolutePath
    }

    companion object {
        const val TAG = "DownloadFileProvider"
        const val FOLDER_NAME = "manga"
    }
}