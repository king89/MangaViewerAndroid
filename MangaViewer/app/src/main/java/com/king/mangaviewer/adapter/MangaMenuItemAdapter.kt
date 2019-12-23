package com.king.mangaviewer.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import com.king.mangaviewer.R
import com.king.mangaviewer.model.LoadingState
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.GlideImageHelper
import com.king.mangaviewer.util.glide.CropImageTransformation
import io.reactivex.disposables.CompositeDisposable

open class MangaMenuItemAdapter(
    protected val listener: MangaMenuAdapterListener? = null) :
    BaseRecyclerViewAdapter<MangaMenuItem, androidx.recyclerview.widget.RecyclerView.ViewHolder>(
        diffCallBack) {

    private var mLoadingState: LoadingState = Idle

    override fun getItemCount(): Int {
        return super.getItemCount() + if (mLoadingState is Loading) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FOOTER -> {
                FooterViewHolder.createHolder(parent)
            }
            else -> {
                createDataViewHolder(parent)
            }

        }
    }

    protected open fun createDataViewHolder(
        parent: ViewGroup) = DataViewHolder.createHolder(parent, R.layout.list_manga_menu_item)

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int) {
        when (holder) {
            is FooterViewHolder -> {

            }
            is DataViewHolder -> {
                val item = getItem(position)
                holder.onBind(item, listener)
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

    override fun onViewRecycled(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        holder as RecyclerViewHolders
        holder.recycle()
        super.onViewRecycled(holder)
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    abstract class RecyclerViewHolders(
        itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        open fun recycle() {
        }

        open fun onBind(item: MangaMenuItem, listener: MangaMenuAdapterListener?) {}
    }

    open class DataViewHolder(itemView: View) : RecyclerViewHolders(itemView) {

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

        override fun onBind(item: MangaMenuItem, listener: MangaMenuAdapterListener?) {
            this.also { holder ->
                GlideImageHelper.getMenuCover(holder.imageView, item, CropImageTransformation())
                    .subscribe()
                    .apply { holder.disposable.add(this) }

                val title = item.title
                holder.textView.text = title
                holder.itemView.setOnClickListener {
                    listener?.onItemClicked(holder.imageView, item)
                }
            }
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

    interface MangaMenuAdapterListener {
        fun onItemClicked(view: View, item: MangaMenuItem) {}
    }

    companion object {
        const val TAG = "MangaMenuItemAdapter"
        val TYPE_DATA = 0
        val TYPE_FOOTER = 1

        val diffCallBack = object : DiffUtil.ItemCallback<MangaMenuItem>() {
            override fun areItemsTheSame(oldItem: MangaMenuItem,
                newItem: MangaMenuItem): Boolean {
                return oldItem.hash == newItem.hash
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: MangaMenuItem,
                newItem: MangaMenuItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
