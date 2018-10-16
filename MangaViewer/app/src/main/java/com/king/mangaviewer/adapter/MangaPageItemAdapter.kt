package com.king.mangaviewer.adapter

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaPageItemAdapter.RecyclerViewHolders
import com.king.mangaviewer.model.TitleAndUrl
import com.king.mangaviewer.util.Logger
import kotlinx.android.synthetic.main.list_manga_page_item.scrollView
import java.lang.Exception
import com.bumptech.glide.request.animation.GlideAnimation
import android.graphics.Bitmap
import com.bumptech.glide.request.target.SimpleTarget

class MangaPageItemAdapter(protected var context: Context,
        protected var pageList: List<TitleAndUrl>) :
        RecyclerView.Adapter<RecyclerViewHolders>() {
    override fun onBindViewHolder(holder: RecyclerViewHolders, position: Int) {

        holder.textView.text = "$position"
        val url = "https://wallpaperbrowse.com/media/images/303836.jpg"
//        val imageView = holder.imageView as SubsamplingScaleImageView
//        val target = object : SimpleTarget<Bitmap>() {
//            override fun onResourceReady(bitmap: Bitmap?,
//                    glideAnimation: GlideAnimation<in Bitmap>?) {
//                // do something with the bitmap
//                // for demonstration purposes, let's just set it to an ImageView
//                imageView.setImage(ImageSource.bitmap(bitmap!!)
//                        .tilingDisabled()
//                        .dimensions(1800,1800)
//                )
//            }
//        }
        Glide.with(this.context)
                .load(url)
                .asBitmap()
                .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        // TODO Auto-generated method stub
        return pageList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(
                R.layout.list_manga_page_item, parent, false)
        return RecyclerViewHolders(layoutView)
    }

    inner class RecyclerViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView by lazy {
            itemView.findViewById<View>(R.id.imageView) as ImageView
//            (view as SubsamplingScaleImageView).apply {
//                setMaximumDpi(150)
//                maxScale = 3.0f
//            }
        }
        val progressBar by lazy {
            itemView.findViewById<View>(R.id.progressBar) as ProgressBar
        }
        val textView: TextView by lazy {
            (itemView.findViewById<View>(R.id.textView) as TextView).also {
                it.setOnTouchListener { view, motionEvent ->
                    Logger.d("-=-=", " TextView scroll event $motionEvent")
                    true
                }
            }
        }

        val scrollView by lazy {
            (itemView.findViewById<View>(R.id.scrollView) as HorizontalScrollView).also {
                it.setOnTouchListener { view, motionEvent ->
                    Logger.d("-=-=", "scrollView scroll event $motionEvent")
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }

            }
        }

    }
}
