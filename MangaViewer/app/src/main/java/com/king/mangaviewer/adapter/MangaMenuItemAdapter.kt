package com.king.mangaviewer.adapter

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.model.LoadingState
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.GlideImageHelper
import com.king.mangaviewer.util.glide.CropImageTransformation
import io.reactivex.disposables.CompositeDisposable

open class MangaMenuItemAdapter(
        private val listener: ((view: View, menu: MangaMenuItem) -> Unit)? = null) :
        BaseRecyclerViewAdapter<MangaMenuItem, RecyclerView.ViewHolder>(diffCallBack) {

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
                val item = getItem(position)
                GlideImageHelper.getMenuCover(holder.imageView, item, CropImageTransformation())
                        .subscribe()
                        .apply { holder.disposable.add(this) }

                val title = this.getItem(position).title
                holder.textView.text = title
                holder.itemView.setOnClickListener { listener?.invoke(holder.imageView, item) }
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
            imageView.setImageResource(R.color.manga_place_holder)
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

    companion object {
        const val TAG = "MangaMenuItemAdapter"
        val TYPE_DATA = 0
        val TYPE_FOOTER = 1

        val diffCallBack = object : DiffUtil.ItemCallback<MangaMenuItem>() {
            override fun areItemsTheSame(oldItem: MangaMenuItem?,
                    newItem: MangaMenuItem?): Boolean {
                return oldItem?.hash == newItem?.hash
            }

            override fun areContentsTheSame(oldItem: MangaMenuItem?,
                    newItem: MangaMenuItem?): Boolean {
                return oldItem == newItem
            }
        }
    }
}
