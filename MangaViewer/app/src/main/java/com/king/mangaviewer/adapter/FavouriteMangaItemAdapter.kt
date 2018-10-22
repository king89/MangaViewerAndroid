package com.king.mangaviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.mangaviewer.R
import com.king.mangaviewer.model.FavouriteMangaMenuItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.viewmodel.MangaViewModel

/**
 * Created by KinG on 6/18/2016.
 */
class FavouriteMangaItemAdapter(context: Context, viewModel: MangaViewModel,
        private val menu: List<MangaMenuItem>) :
        MangaMenuItemAdapter(context, viewModel, menu) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_favourite_manga_menu_item, parent, false)
        return RecyclerViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        super.onBindViewHolder(holder, position)

        val count = (menu[position] as FavouriteMangaMenuItem).updateCount
        holder.countTextView?.apply {
            visibility = View.VISIBLE
            if (count in 1..99) {
                text = count.toString()
            } else if (count > 99) {
                text = "99+"
            } else {
                visibility = View.INVISIBLE
            }
        }
    }

}
