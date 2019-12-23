package com.king.mangaviewer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.RecyclerViewHolders
import com.king.mangaviewer.domain.data.local.DownloadState
import com.king.mangaviewer.domain.data.local.DownloadState.DOWNLOADING
import com.king.mangaviewer.domain.data.local.DownloadState.ERROR
import com.king.mangaviewer.domain.data.local.DownloadState.FINISHED
import com.king.mangaviewer.domain.data.local.DownloadState.NONE
import com.king.mangaviewer.domain.data.local.DownloadState.PENDING
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
            holder.ivState.setOnClickListener {
                //TODO resume or cancel
            }
            holder.setRead(stateMap[item.hash]?.isRead ?: false)
            holder.setDownloadState(stateMap[item.hash]?.downloaded ?: NONE)
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

    inner class RecyclerViewHolders(
        itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val textView: TextView by lazy { itemView.findViewById<View>(R.id.textView) as TextView }
        val viewHeader: View by lazy { itemView.findViewById<View>(R.id.viewHeader) as View }
        val ivState by lazy { itemView.findViewById<ImageView>(R.id.ivState) }
        val cbDownload by lazy { itemView.findViewById<CheckBox>(R.id.cbDownload) }
        val progressBar by lazy { itemView.findViewById<ProgressBar>(R.id.progressBar) }

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
                NONE -> {
                    ivState.visibility = INVISIBLE
                    progressBar.visibility = INVISIBLE
                }
                FINISHED -> {
                    ivState.visibility = VISIBLE
                    progressBar.visibility = INVISIBLE
                    ivState.setImageResource(R.drawable.ic_checked)
                }
                PENDING -> {
                    ivState.visibility = VISIBLE
                    progressBar.visibility = INVISIBLE
                    ivState.setImageResource(R.drawable.ic_pending)
                }
                DOWNLOADING -> {
                    ivState.visibility = INVISIBLE
                    progressBar.visibility = VISIBLE
                    ivState.setImageResource(R.drawable.ic_downloading)
                }
                ERROR -> {
                    ivState.visibility = VISIBLE
                    progressBar.visibility = INVISIBLE
                    ivState.setImageResource(R.drawable.ic_error)
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
            override fun areItemsTheSame(oldItem: MangaChapterItem,
                newItem: MangaChapterItem): Boolean {
                return oldItem.hash == newItem.hash

            }

            override fun areContentsTheSame(oldItem: MangaChapterItem,
                newItem: MangaChapterItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
