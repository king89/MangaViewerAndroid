package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.RecyclerViewHolders
import com.king.mangaviewer.model.MangaChapterItem

class MangaChapterItemAdapter(private val context: Context,
    private val onItemClickListener: OnItemClickListener) :
    BaseRecyclerViewAdapter<MangaChapterItem, RecyclerViewHolders>(diffCallBack) {

    private val MAX_TITLE_LENGTH = 20
    private val isReadArray = SparseBooleanArray()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_manga_chapter_item, parent, false)
        return ChapterViewHolders(layoutView)

    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        val item = getItem(position)
        holder as ChapterViewHolders
        item?.apply {
            var chapterTitle = title
            if (menu.title.length > MAX_TITLE_LENGTH) {
                chapterTitle = chapterTitle.replace(menu.title,
                    context.getString(R.string.prefix_chapter_title))
                title = chapterTitle
            }
            holder.textView.text = chapterTitle
            holder.itemView.setOnClickListener { onItemClickListener.onClick(this) }
            holder.setRead(isReadArray[position])
        }
    }

    fun setRead(pos: Int, value: Boolean = true) {
        if (pos < this.itemCount && pos >= 0) {
            isReadArray.put(pos, value)
            notifyItemChanged(pos)
        }
    }

    fun submitStateList(list: List<MangaChapterStateItem>) {
        if (list.size != itemCount) return

        list.forEachIndexed { index, item ->
            if (item.isRead) {
                setRead(index)
            }
        }
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

    interface OnItemClickListener {
        fun onClick(chapter: MangaChapterItem)
    }

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<MangaChapterItem>() {
            override fun areItemsTheSame(oldItem: MangaChapterItem?,
                newItem: MangaChapterItem?): Boolean {
                return oldItem?.hash == newItem?.hash

            }

            override fun areContentsTheSame(oldItem: MangaChapterItem?,
                newItem: MangaChapterItem?): Boolean {
                return oldItem == newItem
            }
        }
    }
}
