package com.king.mangaviewer.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CUSTOM
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ZOOM_FOCUS_FIXED
import com.king.mangaviewer.R
import com.king.mangaviewer.component.ReadingDirection.LTR
import com.king.mangaviewer.component.ReadingDirection.RTL
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.Logger
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.list_manga_page_item_v2.view.clError
import kotlinx.android.synthetic.main.list_manga_page_item_v2.view.clLoading
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.math.max

class PageView @JvmOverloads constructor(val ctx: Context, attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FrameLayout(ctx, attrs, defStyleAttr), View.OnClickListener {

    private var mData: MangaUri? = null
    private val disposable = CompositeDisposable()
    private val imageView: SubsamplingScaleImageView by lazy {
        findViewById<SubsamplingScaleImageView>(R.id.imageView)
    }
    var readingDirection: ReadingDirection = LTR

    init {
        View.inflate(ctx, R.layout.list_manga_page_item_v2, this)
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_FIXED)
        imageView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_INSIDE)
        imageView.setMinimumScaleType(SCALE_TYPE_CUSTOM)
        imageView.setMinimumTileDpi(180)
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

        findViewById<Button>(R.id.btRetry).setOnClickListener {
            refresh()
        }
    }

    private fun onImageDisplayFailed(e: Exception?) {
        Logger.e(TAG, e, "onImageDisplayFailed")
        showError()
    }

    private fun onLoadingComplete() {
        Logger.d(TAG, "onLoadingComplete")
        hideLoading()
    }

    //    private val MAX_WIDTH: Int = 2560
//    private val MAX_HEIGHT: Int = 1920
//    var newBitmap: Bitmap? = null
    var target = object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap,
                transition: Transition<in Bitmap>?) {
            showImage()
            try {
                val width = resource.width.toFloat()
                val height = resource.height.toFloat()

                val displayMetrics = DisplayMetrics()
                (context as? Activity)?.windowManager
                        ?.defaultDisplay
                        ?.getMetrics(displayMetrics)
                val (screenWidth, screenHeight) = if (displayMetrics.widthPixels == 0) {
                    Pair(width, height)
                } else {
                    Pair(displayMetrics.widthPixels.toFloat(),
                            displayMetrics.heightPixels.toFloat())
                }
                Logger.d(TAG, "screen width: $screenWidth, image width: $width")

                if (context.resources.configuration.orientation == ORIENTATION_PORTRAIT) {
                    if (width > height) {
                        val factor = screenWidth / (width / 2)
                        val imageState = when (readingDirection) {
                            LTR -> ImageViewState(factor, PointF(0f, 0f), 0)
                            RTL -> ImageViewState(factor, PointF(width, 0f), 0)
                        }
                        imageView.minScale = factor
                        imageView.maxScale = max(factor * 2, 2f)
                        imageView.setImage(ImageSource.cachedBitmap(resource), imageState)
                    } else {
                        val factor = screenWidth / width
                        val imageState = if (height > screenHeight) {
                            ImageViewState(factor, PointF(0f, -height), 0)
                        } else {
                            ImageViewState(factor, PointF(0f, 0f), 0)
                        }
                        imageView.minScale = factor
                        imageView.maxScale = max(factor * 2, 2f)
                        imageView.setImage(ImageSource.cachedBitmap(resource), imageState)

                    }
                } else {
                    val factor = screenWidth / width
                    val imageState = ImageViewState(factor, PointF(0f, 0f), 0)

                    imageView.minScale = factor
                    imageView.maxScale = max(factor * 2, 2f)
                    imageView.setImage(ImageSource.cachedBitmap(resource), imageState)
                }
            } catch (e: OutOfMemoryError) {
                Logger.e(TAG, e, "Out of memory when setting image")
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
//                    .override(MAX_WIDTH, MAX_HEIGHT)
                    .downsample(DownsampleStrategy.AT_MOST)
                    .into(target)
        }.delay(500, MILLISECONDS)
                .subscribe()
                .apply { disposable.add(this) }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        refresh()
    }

    private fun showLoading() {
        Logger.d(TAG,
                "show Loading")
        clError.visibility = View.GONE
        clLoading.visibility = View.VISIBLE
        imageView.visibility = View.GONE
    }

    private fun hideLoading() {
        clLoading.visibility = View.GONE
    }

    private fun showImage() {
        Logger.d(TAG,
                "show image")
        clError.visibility = View.GONE
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
        disposable.clear()
        GlideApp.with(this).clear(target)
    }

    companion object {
        val TAG = "PageView"
    }

    fun refresh() {
        mData?.run {
            setData(this)
        }
    }
}

