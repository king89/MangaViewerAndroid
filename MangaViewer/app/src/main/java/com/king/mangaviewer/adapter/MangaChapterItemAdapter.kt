package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.WrapperType.CATEGORY
import com.king.mangaviewer.adapter.WrapperType.CHAPTER
import com.king.mangaviewer.adapter.WrapperType.LAST_READ
import com.king.mangaviewer.model.MangaChapterItem

class MangaChapterItemAdapter(protected val context: Context,
        protected val onItemClickListener: OnItemClickListener,
        protected val dataList: List<MangaChapterItemWrapper>) :
        RecyclerView.Adapter<MangaChapterItemAdapter.RecyclerViewHolders>() {

    private val MAX_TITLE_LENGTH = 20

    override fun getItemCount(): Int {
        // TODO Auto-generated method stub
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        return when (viewType) {
            LAST_READ,
            CHAPTER -> {
                val layoutView = LayoutInflater.from(parent.context).inflate(
                        R.layout.list_manga_chapter_item, parent, false)
                ChapterViewHolders(layoutView)
            }
            CATEGORY -> {
                val view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,
                        parent, false)
                CategoryViewHolders(view)
            }
            else -> {
                RecyclerViewHolders(View(context))
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        when (getItemViewType(position)) {
            LAST_READ,
            CHAPTER -> {
                holder as ChapterViewHolders
                dataList[position].chapter?.apply {
                    var chapterTitle = title
                    if (menu.title.length > MAX_TITLE_LENGTH) {
                        chapterTitle = chapterTitle.replace(menu.title,
                                context.getString(R.string.prefix_chapter_title))
                        title = chapterTitle
                    }
                    holder.textView.text = chapterTitle
                    holder.itemView.setOnClickListener { onItemClickListener.onClick(this) }
                }
            }
            CATEGORY -> {
                holder as CategoryViewHolders
                holder.textView.text = dataList[position].displayName
            }

            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    open class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ChapterViewHolders(itemView: View) : RecyclerViewHolders(itemView) {
        val textView: TextView by lazy { itemView.findViewById<View>(R.id.textView) as TextView }
    }

    inner class CategoryViewHolders(itemView: View) : RecyclerViewHolders(itemView) {
        val textView: TextView by lazy { itemView.findViewById<View>(android.R.id.text1) as TextView }
    }

    interface OnItemClickListener {
        fun onClick(chapter: MangaChapterItem)
    }

}
