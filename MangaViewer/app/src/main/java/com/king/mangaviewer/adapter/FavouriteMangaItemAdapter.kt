package com.king.mangaviewer.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.mangaviewer.R
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.MangaMenuItem

/**
 * Created by KinG on 6/18/2016.
 */
class FavouriteMangaItemAdapter(private val listener: OnItemClickListener? = null) :
        MangaMenuItemAdapter(listener) {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (holder is DataViewHolder) {
            val count = (mDataList[position] as FavouriteMangaMenuItem).updateCount
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

    override fun getDataViewHolderRes(): Int {
        return R.layout.list_favourite_manga_menu_item
    }
}
