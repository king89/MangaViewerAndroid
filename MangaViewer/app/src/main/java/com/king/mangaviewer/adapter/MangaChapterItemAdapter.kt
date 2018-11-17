package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.RecyclerViewHolders
import com.king.mangaviewer.adapter.WrapperType.CATEGORY
import com.king.mangaviewer.adapter.WrapperType.CHAPTER
import com.king.mangaviewer.adapter.WrapperType.LAST_READ
import com.king.mangaviewer.model.MangaChapterItem

class MangaChapterItemAdapter(private val context: Context,
        private val onItemClickListener: OnItemClickListener) :
        BaseRecyclerViewAdapter<MangaChapterItemWrapper, RecyclerViewHolders>(diffCallBack) {

    private val MAX_TITLE_LENGTH = 20

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
        val item = getItem(position)
        when (getItemViewType(position)) {
            LAST_READ,
            CHAPTER -> {
                holder as ChapterViewHolders
                item.chapter?.apply {
                    var chapterTitle = title
                    if (menu.title.length > MAX_TITLE_LENGTH) {
                        chapterTitle = chapterTitle.replace(menu.title,
                                context.getString(R.string.prefix_chapter_title))
                        title = chapterTitle
                    }
                    holder.textView.text = chapterTitle
                    holder.itemView.setOnClickListener { onItemClickListener.onClick(this) }
                    holder.setRead(item.isRead)
                }
            }
            CATEGORY -> {
                holder as CategoryViewHolders
                holder.textView.text = getItem(position).displayName
            }

            else -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    open class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView)
    inner class ChapterViewHolders(itemView: View) : RecyclerViewHolders(itemView) {
        val textView: TextView by lazy { itemView.findViewById<View>(R.id.textView) as TextView }
        val viewHeader: View by lazy { itemView.findViewById<View>(R.id.viewHeader) as View }

        fun setRead(read: Boolean) {
            val bannerColor = if (read) {
                ContextCompat.getColor(context, R.color.color_read_banner)
            } else {
                ContextCompat.getColor(context, R.color.color_unread_banner)
            }
            viewHeader.setBackgroundColor(bannerColor)
        }
    }

    inner class CategoryViewHolders(itemView: View) : RecyclerViewHolders(itemView) {
        val textView: TextView by lazy {
            itemView.findViewById<View>(android.R.id.text1) as TextView
        }
    }

    interface OnItemClickListener {
        fun onClick(chapter: MangaChapterItem)
    }

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<MangaChapterItemWrapper>() {
            override fun areItemsTheSame(oldItem: MangaChapterItemWrapper?,
                    newItem: MangaChapterItemWrapper?): Boolean {

                return when {
                    oldItem?.type == newItem?.type -> {
                        if (oldItem?.type == CHAPTER) {
                            oldItem.chapter?.hash == newItem?.chapter?.hash
                        } else {
                            oldItem?.displayName == newItem?.displayName
                        }
                    }
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: MangaChapterItemWrapper?,
                    newItem: MangaChapterItemWrapper?): Boolean {
                return oldItem == newItem
            }
        }
    }
}
