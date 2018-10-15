package com.king.mangaviewer.activity

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.widget.SwitchCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.component.HasFullScreenControl
import com.king.mangaviewer.component.ReaderListener
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.GsonHelper
import com.king.mangaviewer.viewmodel.MangaViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manga_page_v2.progressBar

class MangaPageActivityV2 : BaseActivity(), HasFullScreenControl, ReaderListener {

    override var isFullScreen: Boolean = false

    private lateinit var mDecorView: View
    private lateinit var sb: SeekBar

    lateinit var mMangaViewModel: MangaViewModel
    lateinit var mSettingViewModel: SettingViewModel

    internal var mIsLoadFromHistory: Boolean = false
    internal var mUpdateConsumer: Consumer<Any> = Consumer { update(null) }

    private val mFFImageButton by lazy { findViewById<View>(R.id.ffButton) as ImageButton }
    private val mFRImageButton by lazy { findViewById<View>(R.id.frButton) as ImageButton }
    private val tvProgress by lazy { findViewById<View>(R.id.textView_pageNum) as TextView }

    protected var mReaderFragment: ReaderFragment? = null

    private val DELAY = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        initViewModels()
        super.onCreate(savedInstanceState)
        //ad
        val handler = Handler()
//        handler.postDelayed({ InitAd() }, DELAY)
        hideSystemUI()
    }

    protected fun initViewModels() {
        mMangaViewModel = appViewModel.Manga
        mSettingViewModel = appViewModel.Setting
    }

    override fun getActionBarTitle(): String {
        // TODO Auto-generated method stub
        return mangaViewModel?.selectedMangaChapterItem?.title ?: ""
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun initControl() {
        // TODO Auto-generated method stub
        mIsLoadFromHistory = intent.getBooleanExtra(INTENT_EXTRA_FROM_HISTORY, false)
        setContentView(R.layout.activity_manga_page_v2)

        val controlsView = findViewById<View>(R.id.fullscreen_content_controls)

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
                controlsView.visibility = View.VISIBLE
                supportActionBar!!.show()

            } else {
                // TODO: The system bars are NOT visible. Make any desired
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
                isFullScreen = true

                controlsView.animate()
                        .translationY(mControlsHeight.toFloat()).duration = mShortAnimTime.toLong()
                controlsView.visibility = View.GONE
                supportActionBar!!.hide()
            }
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

        Observable.fromCallable {
            getMangaHelper().GetPageList(mangaViewModel.selectedMangaChapterItem)
        }
                .subscribeOn(Schedulers.io())
                .flatMapIterable {
                    it
                }
                .map {
                    it.apply {
                        webImageUrl = mangaHelper.getWebImageUrl(it)
                    }
                    MangaUri(it.webImageUrl, it.referUrl)
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoading()
                }
                .doAfterTerminate {
                    hideLoading()
                }
                .subscribe { it ->
                    setupReader(it)
                }
                .apply { compositeDisposable.add(this) }

    }

    fun syncTextView() {
        val totalNum = sb.max
        val currentPage = sb.progress
        tvProgress.text = "$currentPage/$totalNum"
    }

    private fun setupReader(mangaList: List<MangaUri>) {
        val json = GsonHelper.toJson(mangaList)
        val fragment = ViewPagerReaderFragment.newInstance(json)
        fragment.readerListener = this
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
        seekBar.max = mReaderFragment?.getTotalPageNum() ?: 0
        seekBar.progress = mReaderFragment?.getCurrentPageNum()?.plus(1) ?: 0
    }

    override fun onPageChanged(currentPage: Int) {
        syncControlPanel()
    }

    override fun update(msg: Message?) {
        this.supportActionBar!!.title = actionBarTitle
        //add to history
        this.appViewModel.HistoryManga.addChapterItemToHistory(
                mMangaViewModel.selectedMangaChapterItem)

    }

    override fun goBack() {
        mMangaViewModel.nowPagePosition = 0
        mMangaViewModel.mangaPageList = null
        super.goBack()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.page_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.menu_setting) {

//            mViewFlipper!!.stopAutoFullscreen()
            val v = findViewById<View>(R.id.menu_setting)
            displayPopupWindow(v)

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayPopupWindow(anchorView: View) {
        val popup = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.menu_page_setting, null)

        //Init Switch
        val isFTRSwitch = layout.findViewById<View>(R.id.LTRSwitch) as SwitchCompat
        val splitPageSwitch = layout.findViewById<View>(R.id.splitPageSwitch) as SwitchCompat

        isFTRSwitch.isChecked = mSettingViewModel.isFromLeftToRight
        isFTRSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mSettingViewModel.isFromLeftToRight = isChecked
//            mViewFlipper!!.refresh()
        }
        splitPageSwitch.isChecked = mSettingViewModel.getIsSplitPage(this)
        splitPageSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mSettingViewModel.setIsSplitPage(this@MangaPageActivityV2, isChecked)
//            mViewFlipper!!.refresh()
        }

        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        // Show anchored to button
        popup.showAsDropDown(anchorView)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
        window.decorView.invalidate()
    }

    override fun showLoading() {
        progressBar.visibility = VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = GONE

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
