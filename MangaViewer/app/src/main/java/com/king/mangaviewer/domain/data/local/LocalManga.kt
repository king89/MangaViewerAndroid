package com.king.mangaviewer.domain.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.king.mangaviewer.model.MangaMenuItem
import org.joda.time.DateTime

@Entity(tableName = "local_manga")
class LocalManga(
    @PrimaryKey
    @ColumnInfo(name = "hash")
    var hash: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "imagePath")
    var imagePath: String,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "manga_websource_id")
    var manga_websource_id: Int,

    @ColumnInfo(name = "updated_date")
    var updated_date: String

) {
    constructor(mangaItem: MangaMenuItem) : this(
        mangaItem.hash,
        mangaItem.title,
        mangaItem.description,
        mangaItem.imagePath,
        mangaItem.url,
        mangaItem.mangaWebSource.id,
        DateTime.now().toString()
    )
}
