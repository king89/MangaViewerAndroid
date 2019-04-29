package com.king.mangaviewer.model

import java.io.Serializable

@Deprecated("")
open class BaseItem
/**
 * @param id
 * @param title
 * @param description
 * @param imagePath
 */
(var id: String, open var title: String, var description: String,
        var imagePath: String, var url: String, var mangaWebSource: MangaWebSource) : Serializable {

}
