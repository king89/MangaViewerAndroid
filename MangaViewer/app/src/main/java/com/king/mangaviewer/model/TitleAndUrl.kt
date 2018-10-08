package com.king.mangaviewer.model

data class TitleAndUrl @JvmOverloads constructor(
        var title: String,
        var url: String,
        var imagePath: String = "") : Comparable<TitleAndUrl> {

    override fun compareTo(other: TitleAndUrl): Int {
        return this.title.compareTo(other.title)
    }
}
