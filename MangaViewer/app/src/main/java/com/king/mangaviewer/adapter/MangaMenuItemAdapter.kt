package com.king.mangaviewer.adapter

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
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelper
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.HashMap

open class MangaMenuItemAdapter(menu: List<MangaMenuItem>,
        private val listener: OnItemClickListener? = null) :
        BaseRecyclerViewAdapter<MangaMenuItem, MangaMenuItemAdapter.RecyclerViewHolders>() {

    private val mStateHash: HashMap<String, Any>? = null
    private val isFavouriteMangaMenu: Boolean = false
    private lateinit var endlessScrollListener: EndlessScrollListener

    init {
        mDataList = menu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.list_manga_menu_item,
                parent, false)
        return RecyclerViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {
        val item = mDataList!![position]
        Single.fromCallable {
            val url = MangaHelper.getMenuCover(item)
            val header = LazyHeaders.Builder().addHeader("Referer", item.url).build()
            GlideUrl(url, header)
        }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    holder.imageView.setImageResource(R.color.manga_place_holder)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it: GlideUrl ->
                    GlideApp.with(holder.imageView)
                            .load(it)
                            .override(320, 320)
                            .placeholder(R.color.manga_place_holder)
                            .into(holder.imageView)
                }, { Logger.e(TAG, it) })
                .apply { holder.disposable.add(this) }

        val title = this.mDataList!![position].title
        holder.textView.text = title
        holder.itemView.setOnClickListener { listener?.onClick(item) }
    }

    override fun onViewRecycled(holder: RecyclerViewHolders) {
        holder.disposable.clear()
        super.onViewRecycled(holder)
    }

    override fun getItemId(position: Int): Long {
        // TODO Auto-generated method stub
        return position.toLong()
    }

    fun setEndlessScrollListener(endlessScrollListener: EndlessScrollListener) {
        this.endlessScrollListener = endlessScrollListener
    }

    inner class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val disposable = CompositeDisposable()
        var textView: TextView
        var countTextView: TextView?
        var imageView: ImageView

        init {
            imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
            textView = itemView.findViewById<View>(R.id.textView) as TextView
            countTextView = itemView.findViewById<View>(R.id.countTextView) as? TextView
        }

//        override fun onClick(view: View) {
//            val menuPos = adapterPosition
//            viewModel.selectedMangaMenuItem = menu[menuPos]
//            context.startActivity(Intent(context, MangaChapterActivity::class.java))
//            (context as Activity).overridePendingTransition(R.anim.in_rightleft,
//                    R.anim.out_rightleft)
//
//        }
    }



    interface OnItemClickListener {
        fun onClick(menu: MangaMenuItem)
    }

    interface EndlessScrollListener {
        /**
         * Loads more data.
         *
         * @param position
         * @return true loads data actually, false otherwise.
         */
        fun onLoadMore(menuList: List<MangaMenuItem>, state: HashMap<String, Any>)
    }

    companion object {
        const val TAG = "MangaMenuItemAdapter"
    }
}
