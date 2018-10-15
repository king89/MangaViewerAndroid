package com.king.mangaviewer.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.Drawable
import android.support.design.widget.Snackbar
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ZOOM_FOCUS_FIXED
import com.king.mangaviewer.R
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.Logger
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_manga_page_item_v2.view.clError
import kotlinx.android.synthetic.main.list_manga_page_item_v2.view.clLoading
import java.util.concurrent.TimeUnit.MILLISECONDS

class PageView @JvmOverloads constructor(val ctx: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FrameLayout(ctx, attrs, defStyleAttr), View.OnClickListener {

    private var mData: MangaUri? = null
    private val disposable = CompositeDisposable()
    private val imageView: SubsamplingScaleImageView by lazy {
        findViewById<SubsamplingScaleImageView>(R.id.imageView)
    }

    init {
        View.inflate(ctx, R.layout.list_manga_page_item_v2, this)
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED)
        imageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)
        imageView.setMinimumDpi(90)
        imageView.setMinimumTileDpi(180)
        imageView.maxScale = 2f
        imageView.setDoubleTapZoomStyle(ZOOM_FOCUS_FIXED)
        imageView.setOnImageEventListener(object :
                SubsamplingScaleImageView.DefaultOnImageEventListener() {

            override fun onReady() {
                onLoadingComplete()
            }

            override fun onImageLoadError(e: Exception?) {
                onImageDisplayFailed(e)
            }
        })

    }

    private fun onImageDisplayFailed(e: Exception?) {
        Logger.e(TAG, "onImageDisplayFailed", e)
        showError()
    }

    private fun onLoadingComplete() {
        Logger.d(TAG, "onLoadingComplete")
    }

    private val MAX_WIDTH: Int = 2840
    private val MAX_HEIGHT: Int = 1920
    var newBitmap: Bitmap? = null
    var target = object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap,
                transition: Transition<in Bitmap>?) {
            showImage()
            try {
                val newBitmap = resource.copy(resource.config, false)
                imageView.setImage(ImageSource.bitmap(newBitmap))
            } catch (e: OutOfMemoryError) {
                Logger.e(TAG, "Out of memory when setting image", e)
                showError()
            }

        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            showError()
        }
    }

    fun setData(data: MangaUri) {
        mData = data
        val webImageUrl = data.imageUri
        val UserAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5"
        val builder = LazyHeaders.Builder()
        builder.addHeader("Referer", data.referUri)
        builder.addHeader("User-Agent", UserAgent)
        val glideUrl = GlideUrl(webImageUrl, builder.build())
        Logger.d(TAG,
                "Download Image Url: " + "\n Referrer Url: " + data.referUri)

        Observable.fromCallable {
            showLoading()
            GlideApp.with(this)
                    .asBitmap()
                    .load(glideUrl)
                    .override(MAX_WIDTH, MAX_HEIGHT)
                    .downsample(DownsampleStrategy.AT_MOST)
                    .into(target)
        }.delay(500, MILLISECONDS)
                .subscribe()
                .apply { disposable.add(this) }
    }

    private fun showLoading() {
        Logger.d(TAG,
                "show Loading")
        clError.visibility = View.GONE
        clLoading.visibility = View.VISIBLE
        imageView.visibility = View.GONE
    }

    private fun showImage() {
        Logger.d(TAG,
                "show image")

        clError.visibility = View.GONE
        clLoading.visibility = View.GONE
        imageView.visibility = View.VISIBLE
    }

    private fun showError() {
        Logger.d(TAG, "show error")
        clError.visibility = View.VISIBLE
        imageView.visibility = View.GONE
        clLoading.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setTapDetector(detector: GestureDetector) {
        imageView.setOnTouchListener { v, event -> detector.onTouchEvent(event) }
    }

    fun recycle() {
        Logger.d(TAG, "recycled $mData")
//        imageView.setImageDrawable(null)
        imageView.recycle()
        newBitmap?.recycle()
        disposable.clear()
        GlideApp.with(this).clear(target)
    }

    companion object {
        val TAG = "PageView"
    }
}