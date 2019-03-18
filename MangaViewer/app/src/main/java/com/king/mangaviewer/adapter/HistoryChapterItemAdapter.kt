package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.common.Constants.DATE_FORMAT_SHORT
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.util.GlideImageHelper
import com.king.mangaviewer.util.SwipeViewHolder
import com.king.mangaviewer.util.glide.CropImageTransformation
import com.king.mangaviewer.util.toDateTime
import io.reactivex.disposables.CompositeDisposable

class HistoryChapterItemAdapter(private val context: Context,
        private val onItemClickListener: ((view: View, chapter: HistoryMangaChapterItem) -> Unit)? = null,
        private val onButtonClickListener: ((view: View, chapter: HistoryMangaChapterItem) -> Unit)? = null) :
        BaseRecyclerViewAdapter<HistoryMangaChapterItem, HistoryChapterItemAdapter.RecyclerViewHolders>(
                diffCallBack) {

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_history_chapter_item, parent, false)
        return RecyclerViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        val item = getItem(position)
        GlideImageHelper.getMenuCover(holder.imageView, item.menu, CropImageTransformation())
                .subscribe()
                .apply { holder.disposable.add(this) }

        holder.titleTextView.text = item.menu.title
        holder.chapterTextView.text = item.title
        holder.dateTextView.text = item.lastReadDate.toDateTime().toString(DATE_FORMAT_SHORT)
        holder.sourceTextView.text = item.mangaWebSource.displayName
        holder.btContinue.setOnClickListener {
            onButtonClickListener?.invoke(holder.imageView, getItem(holder.adapterPosition))
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(holder.imageView, getItem(holder.adapterPosition))
        }
    }

    override fun onViewRecycled(holder: RecyclerViewHolders) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    fun getItemByPos(position: Int): HistoryMangaChapterItem =
            getItem(position)

    inner class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView),
            SwipeViewHolder {

        var imageView: ImageView
        var titleTextView: TextView
        var chapterTextView: TextView
        var dateTextView: TextView
        var sourceTextView: TextView
        val disposable = CompositeDisposable()
        var foregroundView: View
        val btContinue: Button

        fun recycle() {
            imageView.setImageResource(R.mipmap.ic_preloader_background)
            itemView.setOnClickListener {}
            disposable.clear()
        }

        override fun getForeground(): View {
            return foregroundView
        }

        init {
            imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
            titleTextView = itemView.findViewById<View>(R.id.titleTextView) as TextView
            chapterTextView = itemView.findViewById<View>(R.id.chapterTextView) as TextView
            dateTextView = itemView.findViewById<View>(R.id.dateTextView) as TextView
            sourceTextView = itemView.findViewById<View>(R.id.sourceTextView) as TextView
            foregroundView = itemView.findViewById(R.id.constrainLayout)
            btContinue = itemView.findViewById(R.id.btContinue) as Button
        }

    }

    companion object {
        const val TAG = "HistoryChapterItemAdapter"

        val diffCallBack = object : DiffUtil.ItemCallback<HistoryMangaChapterItem>() {
            override fun areItemsTheSame(oldItem: HistoryMangaChapterItem?,
                    newItem: HistoryMangaChapterItem?): Boolean {
                return oldItem?.hash == newItem?.hash
            }

            override fun areContentsTheSame(oldItem: HistoryMangaChapterItem?,
                    newItem: HistoryMangaChapterItem?): Boolean {
                return oldItem == newItem
            }
        }
    }

}
