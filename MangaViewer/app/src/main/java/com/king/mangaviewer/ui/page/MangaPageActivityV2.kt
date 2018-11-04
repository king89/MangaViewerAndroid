package com.king.mangaviewer.ui.page

import android.os.Bundle
import android.os.Message
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.TooltipCompat
import android.view.Gravity
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
import android.widget.Toast
import com.king.mangaviewer.R
import com.king.mangaviewer.R.string
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.ui.page.fragment.ReaderFragment
import com.king.mangaviewer.ui.page.fragment.RtlViewPagerReaderFragment
import com.king.mangaviewer.ui.page.fragment.ViewPagerReaderFragment
import com.king.mangaviewer.component.HasFullScreenControl
import com.king.mangaviewer.component.ReaderListener
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.model.MangaUri
import com.king.mangaviewer.util.GsonHelper
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.MangaHelperV2
import com.king.mangaviewer.viewmodel.MangaViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manga_page_v2.progressBar
import java.util.concurrent.TimeUnit.MILLISECONDS

class MangaPageActivityV2 : BaseActivity(),
        HasFullScreenControl,
        ReaderListener {

    override var isFullScreen: Boolean = false

    private lateinit var mDecorView: View
    private lateinit var sb: SeekBar

    val mMangaViewModel: MangaViewModel by lazy { appViewModel.Manga }
    val mSettingViewModel: SettingViewModel by lazy { appViewModel.Setting }

    internal var mIsLoadFromHistory: Boolean = false
    internal var mUpdateConsumer: Consumer<Any> = Consumer { update(null) }

    private val mFFImageButton by lazy { findViewById<View>(R.id.ffButton) as ImageButton }
    private val mFRImageButton by lazy { findViewById<View>(R.id.frButton) as ImageButton }
    private val tvProgress by lazy { findViewById<View>(R.id.textView_pageNum) as TextView }

    protected var mReaderFragment: ReaderFragment? = null

    private var mMangaList: List<MangaUri>? = null
    private var delayFullScreenDispose: Disposable? = null
    private val DELAY = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUI()
    }

    override fun getActionBarTitle(): String {
        // TODO Auto-generated method stub
        return mMangaViewModel.selectedMangaChapterItem.title ?: ""
    }

    override fun initControl() {
        // TODO Auto-generated method stub
        mIsLoadFromHistory = intent.getBooleanExtra(
                INTENT_EXTRA_FROM_HISTORY, false)
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

                delayFullScreen()
            } else {
                delayFullScreenDispose?.dispose()
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

        loadPages()

    }

    //TODO should move to use case
    private fun getChapterList(menu: MangaMenuItem): Single<List<MangaChapterItem>> {
        val observable = if (mMangaViewModel.mangaChapterList == null) {
            Single.fromCallable {
                mangaHelper.getChapterList(menu)
            }
        } else {
            Single.just(mMangaViewModel.mangaChapterList)
        }
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoading() }
                .doAfterTerminate { hideLoading() }
    }

    override fun prevChapter() {
        //prev chapter, pos len(list) is the oldest chapter

        val menu = mMangaViewModel.selectedMangaChapterItem.menu
        getChapterList(menu)
                .subscribe({
                    mMangaViewModel.mangaChapterList = it

                    val chapterList = mMangaViewModel.mangaChapterList
                    val currentChapter = mMangaViewModel.selectedMangaChapterItem

                    val pos = chapterList.indexOf(currentChapter)
                    if (pos + 1 < chapterList.size) {
                        mMangaViewModel.selectedMangaChapterItem = chapterList[pos + 1]
                        loadPages()
                    } else {
                        Toast.makeText(this, resources.getString(string.no_more_prev_chapter),
                                Toast.LENGTH_SHORT)
                                .apply { setGravity(Gravity.CENTER, 0, 0) }
                                .show()
                    }
                }, {
                    Logger.e(TAG, it, "get chapter list error")
                })
                .apply { compositeDisposable.add(this) }

    }

    override fun nextChapter() {
        //next chapter, pos 0 is the latest chapter
        val menu = mMangaViewModel.selectedMangaChapterItem.menu
        getChapterList(menu)
                .subscribe({
                    mMangaViewModel.mangaChapterList = it

                    val chapterList = mMangaViewModel.mangaChapterList
                    val currentChapter = mMangaViewModel.selectedMangaChapterItem

                    val pos = chapterList.indexOf(currentChapter)
                    if (pos - 1 >= 0) {
                        mMangaViewModel.selectedMangaChapterItem = chapterList[pos - 1]
                        loadPages()
                    } else {
                        Toast.makeText(this, resources.getString(string.no_more_next_chapter),
                                Toast.LENGTH_SHORT)
                                .apply { setGravity(Gravity.CENTER, 0, 0) }
                                .show()
                    }
                }, {
                    Logger.e(TAG, it, "get chapter list error")
                })
                .apply { compositeDisposable.add(this) }
    }

    private fun loadPages() {
        Observable.fromCallable {
            MangaHelperV2.getPageList(mangaViewModel.selectedMangaChapterItem)
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
                    update(null)
                }
                .apply { compositeDisposable.add(this) }
    }

    fun syncTextView() {
        val totalNum = sb.max + 1
        val currentPage = sb.progress + 1
        tvProgress.text = "$currentPage / $totalNum"
    }

    private fun setupReader(mangaList: List<MangaUri>) {
        mMangaList = mangaList
        val json = GsonHelper.toJson(mangaList)

        val fragment = if (mSettingViewModel.getIsFromLeftToRight(this)) {
            ViewPagerReaderFragment.newInstance(json)
        } else {
            RtlViewPagerReaderFragment.newInstance(json)
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

        isFTRSwitch.isChecked = mSettingViewModel.getIsFromLeftToRight(this)
        isFTRSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            mSettingViewModel.setIsFromLeftToRight(this, isChecked)
            mMangaList?.run {
                setupReader(this)
            }
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

    private fun delayFullScreen() {
        delayFullScreenDispose?.dispose()
        delayFullScreenDispose = Single.just("")
                .delay(DELAY, MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: String ->
                    hideSystemUI()
                }
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
