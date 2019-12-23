package com.king.mangaviewer.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseRecyclerViewAdapter<U, T : androidx.recyclerview.widget.RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<U>) : ListAdapter<U, T>(diffCallback) {
}