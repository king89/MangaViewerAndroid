package com.king.mangaviewer.util

import java.io.File

fun File.concat(child: String): File {
    return File(this, child)
}