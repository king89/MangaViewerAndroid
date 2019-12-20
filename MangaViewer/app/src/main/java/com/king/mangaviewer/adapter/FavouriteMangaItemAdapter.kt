package com.king.mangaviewer.adapter

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.mangaviewer.R
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.MangaMenuItem

/**
 * Created by KinG on 6/18/2016.
 */
class FavouriteMangaItemAdapter(
    listener: MangaMenuAdapterListener? = null) :
    MangaMenuItemAdapter(listener) {

    override fun createDataViewHolder(parent: ViewGroup): RecyclerViewHolders {
        return FavDataViewHolder.createHolder(parent, R.layout.list_favourite_manga_menu_item)
    }

    class FavDataViewHolder(itemView: View) : DataViewHolder(itemView) {
        override fun onBind(item: MangaMenuItem, listener: MangaMenuAdapterListener?) {
            this.also { holder ->
                super.onBind(item, listener)
                val count = (item as FavouriteMangaMenuItem).updateCount
                holder.countTextView?.apply {
                    visibility = View.VISIBLE
                    when {
                        count in 1..99 -> text = count.toString()
                        count > 99 -> text = "99+"
                        else -> visibility = View.INVISIBLE
                    }
                }
            }
        }

        companion object {
            fun createHolder(parent: ViewGroup, @LayoutRes layoutRes: Int): RecyclerViewHolders {
                val layoutView = LayoutInflater.from(parent.context).inflate(
                    layoutRes,
                    parent, false)
                return FavDataViewHolder(layoutView)
            }
        }
    }
}
