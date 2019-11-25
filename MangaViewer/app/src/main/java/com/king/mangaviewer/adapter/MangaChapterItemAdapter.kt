package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.DownloadState.Downloaded
import com.king.mangaviewer.adapter.DownloadState.None
import com.king.mangaviewer.adapter.DownloadState.Pending
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.RecyclerViewHolders
import com.king.mangaviewer.model.MangaChapterItem

class MangaChapterItemAdapter(private val context: Context,
    private val onItemClickListener: OnItemClickListener,
    private val onSelectedChangeListener: OnSelectedChangeListener? = null) :
    BaseRecyclerViewAdapter<MangaChapterItem, RecyclerViewHolders>(diffCallBack) {

    private val MAX_TITLE_LENGTH = 20
    private var stateMap: Map<String, MangaChapterStateItem> = emptyMap()
    private var selectableMode = false
    private val selectedMap: MutableMap<Int, Boolean> = hashMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_manga_chapter_item, parent, false)
        return RecyclerViewHolders(layoutView)

    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        val item = getItem(position)
        item?.apply {
            var chapterTitle = title
            if (menu.title.length > MAX_TITLE_LENGTH) {
                chapterTitle = chapterTitle.replace(menu.title,
                    context.getString(R.string.prefix_chapter_title))
                title = chapterTitle
            }
            holder.textView.text = chapterTitle
            holder.itemView.setOnClickListener {
                if (selectableMode) {
                    holder.cbDownload.toggle()
                    toggleSelected(position)
                } else {
                    onItemClickListener.onClick(this)
                }
            }
            holder.cbDownload.setOnClickListener {
                toggleSelected(position)
            }
            holder.setRead(stateMap[item.hash]?.isRead ?: false)
            holder.setDownloadState(stateMap[item.hash]?.downloaded ?: None)
            holder.setSelectable(selectableMode, selectedMap[position] ?: false)
        }
    }

    fun submitStateMap(map: Map<String, MangaChapterStateItem>) {
        stateMap = map
        notifyDataSetChanged()
    }

    fun toggleSelectableMode() {
        selectableMode = selectableMode.not()
        if (!selectableMode) {
            selectedMap.clear()
        }
        notifyDataSetChanged()
        notifySelectedChange()
    }

    private fun toggleSelected(position: Int) {
        val value = selectedMap[position] ?: false

        if (value) {
            selectedMap.remove(position)
        } else {
            selectedMap[position] = true
        }

        notifySelectedChange()
    }

    private fun notifySelectedChange() {
        onSelectedChangeListener?.onChange(
            selectedMap.filter { it.value }
                .map { getItem(it.key) }
        )
    }

    inner class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView by lazy { itemView.findViewById<View>(R.id.textView) as TextView }
        val viewHeader: View by lazy { itemView.findViewById<View>(R.id.viewHeader) as View }
        val ivState by lazy { itemView.findViewById<ImageView>(R.id.ivState) }
        val cbDownload by lazy { itemView.findViewById<CheckBox>(R.id.cbDownload) }

        fun setRead(read: Boolean) {
            val bannerColor = if (read) {
                ContextCompat.getColor(context, R.color.color_read_banner)
            } else {
                ContextCompat.getColor(context, R.color.color_unread_banner)
            }
            viewHeader.setBackgroundColor(bannerColor)
        }

        fun setDownloadState(state: DownloadState) {
            when (state) {
                None -> ivState.visibility = GONE
                Downloaded -> {
                    ivState.visibility = VISIBLE
                    ivState.setImageResource(R.drawable.ic_downloaded)
                }
                Pending -> {
                    ivState.visibility = VISIBLE
                    ivState.setImageResource(R.drawable.ic_pending)
                }
            }
        }

        fun setSelectable(selectable: Boolean, selected: Boolean) {
            if (selectable) {
                cbDownload.visibility = VISIBLE
            } else {
                cbDownload.visibility = GONE
            }
            cbDownload.isChecked = selected
        }
    }

    interface OnItemClickListener {
        fun onClick(chapter: MangaChapterItem)
    }

    interface OnSelectedChangeListener {
        fun onChange(chapterList: List<MangaChapterItem>)
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
