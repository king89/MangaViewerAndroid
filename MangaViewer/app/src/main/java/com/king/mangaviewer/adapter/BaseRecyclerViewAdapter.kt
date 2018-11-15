package com.king.mangaviewer.adapter

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<U, T : RecyclerView.ViewHolder>(
        diffCallback: DiffUtil.ItemCallback<U>) : ListAdapter<U, T>(diffCallback) {
}