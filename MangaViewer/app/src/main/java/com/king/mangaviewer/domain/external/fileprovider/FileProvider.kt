package com.king.mangaviewer.domain.external.fileprovider

import android.content.Context
import android.os.Environment
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject

interface FileProvider {

  fun saveFile(folderPath: String, fileName: String, data: InputStream): Single<String>

  fun zipFolder(folderPath: String, zipFileName: String): Single<String>

  fun moveFile(pathSrc: String, pathDes: String): Completable
}

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

  override fun zipFolder(folderPath: String, zipFileName: String): Single<String> {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun moveFile(pathSrc: String, pathDes: String): Completable {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  companion object {
    const val TAG = "DownloadFileProvider"
    const val FOLDER_NAME = "manga"
  }

}