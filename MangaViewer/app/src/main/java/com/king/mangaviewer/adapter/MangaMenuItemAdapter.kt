package com.king.mangaviewer.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.king.mangaviewer.R
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.LoadingState
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

open class MangaMenuItemAdapter(private val listener: OnItemClickListener? = null) :
        BaseRecyclerViewAdapter<MangaMenuItem, RecyclerView.ViewHolder>() {

    private var mLoadingState: LoadingState = Idle

    override fun getItemCount(): Int {
        return super.getItemCount() + if (mLoadingState is Loading) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FOOTER -> {
                FooterViewHolder.createHolder(parent)
            }
            else -> {
                DataViewHolder.createHolder(parent, getDataViewHolderRes())
            }

        }
    }

    protected open fun getDataViewHolderRes(): Int {
        return R.layout.list_manga_menu_item
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FooterViewHolder -> {

            }
            is DataViewHolder -> {
                val item = mDataList[position]
                Single.fromCallable {
                    val url = MangaHelperV2.getMenuCover(item)
                    if (url.isEmpty()) return@fromCallable Any()
                    val header = LazyHeaders.Builder().addHeader("Referer", item.url).build()
                    GlideUrl(url, header)
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ it: Any ->
                            (it as? GlideUrl) ?: apply {
                                holder.imageView.setImageResource(R.color.manga_place_holder)
                                return@subscribe
                            }
                            GlideApp.with(holder.imageView)
                                    .load(it)
                                    .override(320, 320)
                                    .placeholder(R.color.manga_place_holder)
                                    .into(holder.imageView)
                        }, { Logger.e(TAG, it) })
                        .apply { holder.disposable.add(this) }

                val title = this.mDataList[position].title
                holder.textView.text = title
                holder.itemView.setOnClickListener { listener?.onClick(item) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mLoadingState is Loading && position >= super.getItemCount()) {
            TYPE_FOOTER
        } else {
            TYPE_DATA
        }
    }

    fun getSpanSize(position: Int, total: Int): Int {
        return if (getItemViewType(position) == TYPE_DATA)
            1
        else {
            total
        }
    }

    fun setLoadingState(loadingState: LoadingState) {
        mLoadingState = loadingState
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        holder as RecyclerViewHolders
        holder.recycle()
        super.onViewRecycled(holder)
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    abstract class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun recycle() {
        }
    }

    class DataViewHolder(itemView: View) : RecyclerViewHolders(itemView) {

        val disposable = CompositeDisposable()
        var textView: TextView
        var countTextView: TextView?
        var imageView: ImageView

        init {
            imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
            textView = itemView.findViewById<View>(R.id.textView) as TextView
            countTextView = itemView.findViewById<View>(R.id.countTextView) as? TextView
        }

        override fun recycle() {
            disposable.clear()
        }

        companion object {
            fun createHolder(parent: ViewGroup, @LayoutRes layoutRes: Int): RecyclerViewHolders {
                val layoutView = LayoutInflater.from(parent.context).inflate(
                        layoutRes,
                        parent, false)
                return DataViewHolder(layoutView)
            }
        }

    }

    class FooterViewHolder(itemView: View) : RecyclerViewHolders(itemView) {
        companion object {
            fun createHolder(parent: ViewGroup): RecyclerViewHolders {
                val layoutView = LayoutInflater.from(parent.context).inflate(
                        R.layout.list_item_loading_footer,
                        parent, false)
                return FooterViewHolder(layoutView)
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(menu: MangaMenuItem)
    }

    companion object {
        const val TAG = "MangaMenuItemAdapter"
        val TYPE_DATA = 0
        val TYPE_FOOTER = 1
    }
}
