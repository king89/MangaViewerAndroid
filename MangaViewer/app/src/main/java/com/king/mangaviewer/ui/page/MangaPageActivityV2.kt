package com.king.mangaviewer.ui.page

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_SHORT
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.TooltipCompat
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.king.mangaviewer.R
import com.king.mangaviewer.R.string
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.component.HasFullScreenControl
import com.king.mangaviewer.component.ReaderCallback
import com.king.mangaviewer.component.ReadingDirection
import com.king.mangaviewer.component.ReadingDirection.LTR
import com.king.mangaviewer.component.ReadingDirection.RTL
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.ui.main.MainActivity
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.SubError.NoNextChapter
import com.king.mangaviewer.ui.page.MangaPageActivityV2ViewModel.SubError.NoPrevChapter
import com.king.mangaviewer.ui.page.fragment.ReaderFragment
import com.king.mangaviewer.ui.page.fragment.RtlViewPagerReaderFragment
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.withViewModel
import com.king.mangaviewer.viewmodel.MangaViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_manga_page_v2.progressBar
import kotlinx.android.synthetic.main.list_manga_page_item_v2.clLoading
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class MangaPageActivityV2 : BaseActivity(),
        HasFullScreenControl,
        ReaderCallback {
    @Inject
    @field:ActivityScopedFactory
    lateinit var activityScopedFactory: ViewModelFactory
    lateinit var viewModel: MangaPageActivityV2ViewModel

    override var isFullScreen: Boolean = false

    private lateinit var mDecorView: View
    private lateinit var sb: SeekBar

    val mMangaViewModel: MangaViewModel by lazy { appViewModel.Manga }
    val mSettingViewModel: SettingViewModel by lazy { appViewModel.Setting }

    internal var mIsLoadFromHistory: Boolean = false

    private val mFFImageButton by lazy { findViewById<View>(R.id.ffButton) as ImageButton }
    private val mFRImageButton by lazy { findViewById<View>(R.id.frButton) as ImageButton }
    private val tvProgress by lazy { findViewById<View>(R.id.textView_pageNum) as TextView }
    private val controlsView by lazy { findViewById<View>(R.id.fullscreen_content_controls) }
    private val fabBrightness by lazy { findViewById<FloatingActionButton>(R.id.fabBrightness) }
    private val fabChapters by lazy { findViewById<FloatingActionButton>(R.id.fabChapters) }
    private val fabDirection by lazy { findViewById<FloatingActionButton>(R.id.fabDirection) }
    private val fabRotation by lazy { findViewById<FloatingActionButton>(R.id.fabRotation) }

    protected var mReaderFragment: ReaderFragment? = null

    private var delayFullScreenDispose: Disposable? = null
    private val DELAY = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO recover from last read
        if (savedInstanceState != null) {
            Logger.i(TAG, "Start from beginning")
            //start from the beginning
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
            finish()
            return
        }
        delayFullScreen()
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveCurrentReadPage()
    }

    override fun getActionBarTitle(): String {
        return mMangaViewModel.selectedMangaChapterItem.title
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        stopAutoHideIfNecessary(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun stopAutoHideIfNecessary(ev: MotionEvent) {
        val editTextRect = Rect()
        controlsView.getHitRect(editTextRect)

        if (editTextRect.contains(ev.x.toInt(), ev.y.toInt())) {
            delayFullScreenDispose?.dispose()
        }
    }

    override fun initControl() {
        mIsLoadFromHistory = intent.getBooleanExtra(
                INTENT_EXTRA_FROM_HISTORY, false)
        setContentView(R.layout.activity_manga_page_v2)

        mDecorView = window.decorView
        mDecorView.setOnSystemUiVisibilityChangeListener { visibility ->
            var mControlsHeight = 0
            var mShortAnimTime = 0
            if (mControlsHeight == 0) {
                mControlsHeight = controlsView.height
            }
            if (mShortAnimTime == 0) {
                mShortAnimTime = resources.getInteger(
                        android.R.integer.config_shortAnimTime)
            }
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // TODO: The system bars are visible. Make any desired
                // adjustments to your UI, such as showing the action bar or
                // other navigational controls.
                isFullScreen = false
                controlsView.animate()
                        .translationY(0f).duration = mShortAnimTime.toLong()
                supportActionBar!!.show()

                delayFullScreen()
            } else {
                delayFullScreenDispose?.dispose()
                // TODO: The system bars are NOT visible. Make any desired
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
                isFullScreen = true

                controlsView.animate().apply {
                    translationY(mControlsHeight.toFloat()).duration = mShortAnimTime.toLong()
                }.start()
                supportActionBar!!.hide()
            }
        }

        TooltipCompat.setTooltipText(mFFImageButton,
                getString(R.string.button_tooltip_next_chapter))
        TooltipCompat.setTooltipText(mFRImageButton,
                getString(R.string.button_tooltip_prev_chapter))

        mFFImageButton.setOnClickListener {
            nextChapter()
        }

        mFRImageButton.setOnClickListener {
            prevChapter()
        }

        //seekbar
        sb = findViewById<View>(R.id.seekBar) as SeekBar
        sb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                syncTextView()
                mReaderFragment?.showThumbnail(progress)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                syncSeekBar(seekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val pos = seekBar.progress
                mReaderFragment?.smoothScrollToPage(pos)
            }
        })

        initButtons()
        initViewModel()

    }

    private fun initButtons() {
        fabBrightness.setOnClickListener { }
        fabChapters.setOnClickListener { }
        fabDirection.setOnClickListener {
            viewModel.toggleDirection()
            setupReader()
        }
        fabRotation.setOnClickListener { }
    }

    private fun initViewModel() {
        withViewModel<MangaPageActivityV2ViewModel>(activityScopedFactory) {
            viewModel = this

            loadingState.observe(this@MangaPageActivityV2, Observer {
                when (it) {
                    Loading -> showLoading()
                    Idle -> hideLoading()
                }
            })

            errorMessage.observe(this@MangaPageActivityV2, Observer {
                when (it!!) {
                    NoPrevChapter ->
                        showErrorMessage(resources.getString(string.no_more_prev_chapter))
                    NoNextChapter ->
                        showErrorMessage(resources.getString(string.no_more_next_chapter))
                    else -> {
                        Snackbar.make(this@MangaPageActivityV2.mDecorView,
                                R.string.oops_error_message, LENGTH_SHORT)
                    }
                }
            })

            totalPageNum.observe(this@MangaPageActivityV2, Observer {
                syncTextView()
            })

            selectedChapterName.observe(this@MangaPageActivityV2, Observer {
                supportActionBar!!.title = it
            })

            dataList.observe(this@MangaPageActivityV2, Observer {
                setupReader()
            })

            readingDirection.observe(this@MangaPageActivityV2, Observer {
                when (it!!) {
                    LTR -> {
                        fabDirection.setImageDrawable(
                                ContextCompat.getDrawable(this@MangaPageActivityV2,
                                        R.drawable.ic_reading_ltr))
                    }
                    RTL -> {
                        fabDirection.setImageDrawable(
                                ContextCompat.getDrawable(this@MangaPageActivityV2,
                                        R.drawable.ic_reading_rtl))
                    }
                }
            })
            attachToView()

        }
    }

    private fun showErrorMessage(s: String) {
        Toast.makeText(this, s,
                Toast.LENGTH_SHORT)
                .apply { setGravity(Gravity.CENTER, 0, 0) }
                .show()
    }

    override fun prevChapter() {
        viewModel.prevChapter()
        delayFullScreen()
    }

    override fun nextChapter() {
        viewModel.nextChapter()
        delayFullScreen()
    }

    fun syncTextView() {
        if (sb.max > 0) {
            val totalNum = sb.max + 1
            val currentPage = sb.progress + 1
            viewModel.currentPageNum = sb.progress
            tvProgress.text = "$currentPage / $totalNum"
        } else {
            tvProgress.text = "- / -"
        }
    }

    private fun setupReader() {
        val fragment = if (mSettingViewModel.getIsFromLeftToRight(this)) {
            ViewPagerReaderFragment.newInstance()
        } else {
            RtlViewPagerReaderFragment.newInstance()
        }

        fragment.readerListener = this
        fragment.startPage = 0
        supportFragmentManager?.beginTransaction()
                ?.replace(R.id.readerFragment, fragment)
                ?.commitNow()
        mReaderFragment = fragment
        syncControlPanel()
    }

    private fun syncControlPanel() {
        syncSeekBar(sb)
        syncTextView()
    }

    private fun syncSeekBar(seekBar: SeekBar) {
        seekBar.max = mReaderFragment?.getTotalPageNum()?.minus(1) ?: 0
        seekBar.progress = mReaderFragment?.getCurrentPageNum() ?: 0
    }

    override fun onPageChanged(currentPage: Int) {
        syncControlPanel()
    }

    override fun goBack() {
//        mMangaViewModel.nowPagePosition = 0
//        mMangaViewModel.mangaPageList = null
        super.goBack()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) showSystemUI()
        window.decorView.invalidate()
    }

    private fun delayFullScreen(delayInMill: Long = DELAY) {
        delayFullScreenDispose?.dispose()
        delayFullScreenDispose = Single.just("")
                .delay(delayInMill, MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { _ ->
                    hideSystemUI()
                }
    }

    override fun showLoading() {
        clLoading.visibility = VISIBLE
    }

    override fun hideLoading() {
        clLoading.visibility = GONE
    }

    override fun toggleUI() {
        isFullScreen = !isFullScreen
        if (!isFullScreen) {
            showSystemUI()
        } else {
            hideSystemUI()
        }
    }

    override fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    override fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

    }

    companion object {
        val TAG = "MangaPageActivityV2"
        val INTENT_EXTRA_FROM_HISTORY = "intent_extra_from_history"
    }
}
