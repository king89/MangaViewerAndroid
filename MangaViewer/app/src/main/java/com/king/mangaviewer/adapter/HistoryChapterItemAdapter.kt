package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.Companion
import com.king.mangaviewer.component.MyImageView
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HistoryChapterItemAdapter(private val context: Context,
        private val onClickListener: ((chapter: HistoryMangaChapterItem) -> Unit)? = null) :
        BaseRecyclerViewAdapter<HistoryMangaChapterItem, HistoryChapterItemAdapter.RecyclerViewHolders>() {

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
        Single.fromCallable {
            val url = MangaHelper.getMenuCover(mDataList[position].menu)
            val header = LazyHeaders.Builder().addHeader("Referer",
                    mDataList[position].menu.url).build()
            GlideUrl(url, header)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it: GlideUrl ->
                    GlideApp.with(holder.imageView)
                            .load(it)
                            .override(320, 320)
                            .placeholder(R.color.manga_place_holder)
                            .into(holder.imageView)
                }, { Logger.e(TAG, it) })
                .apply { holder.disposable.add(this) }

        holder.titleTextView.text = mDataList[position].menu.title
        holder.chapterTextView.text = mDataList[position].title
        holder.dateTextView.text = mDataList[position].lastReadDate
        holder.sourceTextView.text = mDataList[position].mangaWebSource.displayName
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(mDataList[position])
        }
    }

    override fun onViewRecycled(holder: RecyclerViewHolders) {
        super.onViewRecycled(holder)
        holder.recycle()
    }

    inner class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imageView: ImageView
        var titleTextView: TextView
        var chapterTextView: TextView
        var dateTextView: TextView
        var sourceTextView: TextView
        val disposable = CompositeDisposable()

        fun recycle() {
            disposable.clear()
        }

        init {
            imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
            titleTextView = itemView.findViewById<View>(R.id.titleTextView) as TextView
            chapterTextView = itemView.findViewById<View>(R.id.chapterTextView) as TextView
            dateTextView = itemView.findViewById<View>(R.id.dateTextView) as TextView
            sourceTextView = itemView.findViewById<View>(R.id.sourceTextView) as TextView
        }

    }

    companion object {
        const val TAG = "HistoryChapterItemAdapter"
    }
}
