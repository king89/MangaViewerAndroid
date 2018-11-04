package com.king.mangaviewer.adapter

import android.support.v7.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<U, T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {
    protected var mDataList: List<U> = emptyList()
    open fun submitList(dataList: List<U>?) {
        mDataList = dataList ?: emptyList()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        // TODO Auto-generated method stub
        return mDataList.size
    }
}