package com.king.mangaviewer.component

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.PointF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CUSTOM
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.ZOOM_FOCUS_FIXED
import com.king.mangaviewer.R
import com.king.mangaviewer.component.ReadingDirection.LTR
import com.king.mangaviewer.component.ReadingDirection.RTL
import com.king.mangaviewer.databinding.ListMangaPageItemV2Binding
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.model.MangaUriType.WEB
import com.king.mangaviewer.model.MangaUriType.ZIP
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.imageloader.GlideImageLoader
import com.king.mangaviewer.util.imageloader.ImageLoader
import com.king.mangaviewer.util.imageloader.ZipImageLoader
import io.reactivex.disposables.CompositeDisposable
import kotlin.math.max

class PageView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(ctx, attrs, defStyleAttr), View.OnClickListener {

    private var mData: MangaUri? = null
    private val disposable = CompositeDisposable()
    private val imageView: SubsamplingScaleImageView by lazy {
        findViewById(R.id.imageView)
    }
    private val clError: View by lazy { findViewById(R.id.clError) }

    var readingDirection: ReadingDirection = LTR
    private var imageLoader: ImageLoader? = null

    private var binding: ListMangaPageItemV2Binding =
        ListMangaPageItemV2Binding.inflate(LayoutInflater.from(context), this, true)

    init {
        imageView.setDoubleTapZoomStyle(ZOOM_FOCUS_FIXED)
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

    fun setData(data: MangaUri) {
        mData = data
        imageLoader = getImageLoader(data)
        imageLoader?.loadImage(imageView, data, readingDirection, {
            showLoading()
        }, { resource ->
            showImage()
            setImage(resource)
        }, { e ->
            Logger.e(TAG, e, "fail to setting image")
            showError()
        })
    }

    private fun getImageLoader(
        data: MangaUri
    ): ImageLoader {
        return when (data.type) {
            WEB -> {
                GlideImageLoader(context)
            }

            ZIP -> {
                ZipImageLoader()
            }
        }
    }

    private fun setImage(resource: Bitmap) {
        val width = resource.width.toFloat()
        val height = resource.height.toFloat()

        val displayMetrics = DisplayMetrics()
        (context as? Activity)?.windowManager
            ?.defaultDisplay
            ?.getMetrics(displayMetrics)
        val (screenWidth, screenHeight) = if (displayMetrics.widthPixels == 0) {
            Pair(width, height)
        } else {
            Pair(
                displayMetrics.widthPixels.toFloat(),
                displayMetrics.heightPixels.toFloat()
            )
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
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        refresh()
    }

    private fun showLoading() {
        Logger.d(
            TAG,
            "show Loading"
        )
        clError.visibility = View.GONE
        binding.apply {
            clLoading.visibility = View.VISIBLE
            imageView.visibility = View.GONE
        }
    }

    private fun hideLoading() {
        binding.apply {
            clLoading.visibility = View.GONE
        }
    }

    private fun showImage() {
        Logger.d(
            TAG,
            "show image"
        )
        clError.visibility = View.GONE
        binding.apply {
            imageView.visibility = View.VISIBLE
        }
    }

    private fun showError() {
        Logger.d(TAG, "show error")

        clError.visibility = View.VISIBLE
        binding.apply {
            imageView.visibility = View.GONE
            clLoading.visibility = View.GONE
        }
    }

    override fun onClick(v: View?) {
        TODO(
            "not implemented"
        ) //To change body of created functions use File | Settings | File Templates.
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
        imageLoader?.clear()
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

