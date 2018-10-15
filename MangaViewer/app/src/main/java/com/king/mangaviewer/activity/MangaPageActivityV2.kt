package com.king.mangaviewer.activity

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.SwitchCompat
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaPageItemAdapter
import com.king.mangaviewer.component.MyViewFlipper
import com.king.mangaviewer.model.TitleAndUrl
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.viewmodel.MangaViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_manga_page_v2.recyclerView
import kotlinx.android.synthetic.main.list_manga_page_item.scrollView

class MangaPageActivityV2 : BaseActivity() {

    private var mDecorView: View? = null
    internal var sb: SeekBar? = null
    lateinit var mAdView: AdView

    lateinit var mMangaViewModel: MangaViewModel
    lateinit var mSettingViewModel: SettingViewModel

    internal var mIsLoadFromHistory: Boolean = false
    internal var mUpdateConsumer: Consumer<Any> = Consumer { update(null) }

    private val mFFImageButton by lazy { findViewById<View>(R.id.ffButton) as ImageButton }
    private val mFRImageButton by lazy { findViewById<View>(R.id.frButton) as ImageButton }

    private val DELAY = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        initViewModels()
        super.onCreate(savedInstanceState)
        //ad
        val handler = Handler()
//        handler.postDelayed({ InitAd() }, DELAY)
    }

    protected fun initViewModels() {
        mMangaViewModel = appViewModel.Manga
        mSettingViewModel = appViewModel.Setting
    }

    override fun getActionBarTitle(): String {
        // TODO Auto-generated method stub
        return ""
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
        mDecorView!!.setOnSystemUiVisibilityChangeListener { visibility ->
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
                //                    isFullScreen = false;
                controlsView.animate()
                        .translationY(0f).duration = mShortAnimTime.toLong()
                controlsView.visibility = View.VISIBLE
                supportActionBar!!.show()

            } else {
                // TODO: The system bars are NOT visible. Make any desired
                // adjustments to your UI, such as hiding the action bar or
                // other navigational controls.
                //                    isFullScreen = true;

                controlsView.animate()
                        .translationY(mControlsHeight.toFloat()).duration = mShortAnimTime.toLong()
                controlsView.visibility = View.GONE
                supportActionBar!!.hide()
            }
        }

        //seekbar
        val tv = findViewById<View>(R.id.textView_pageNum) as TextView
        sb = findViewById<View>(R.id.seekBar) as SeekBar
        sb!!.setOnTouchListener { v, event ->
            false
        }
        sb!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

//                tv.text = "" + (progress + 1) + "/" + mViewFlipper!!.pageCount
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
//                seekBar.max = mViewFlipper!!.pageCount - 1
//                seekBar.progress = mViewFlipper!!.currPos
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val pos = seekBar.progress
//                mViewFlipper!!.goToPageNum(pos)
            }
        })
        //set current pos changed listener
//        mViewFlipper!!.setOnCurrentPosChangedListener { pos ->
//            sb!!.progress = pos
//            sb!!.max = mViewFlipper!!.pageCount - 1
//            tv.text = "" + (pos + 1) + "/" + mViewFlipper!!.pageCount
//        }

        //Image Button

//        mFFImageButton.setOnClickListener { mViewFlipper!!.goNextChapter() }
//        mFRImageButton.setOnClickListener { mViewFlipper!!.goPrevChapter() }

        //start
//        if (!mIsLoadFromHistory) {
//            mViewFlipper!!.initial(mMangaViewModel, mSettingViewModel, mUpdateConsumer)
//        } else {
//            mViewFlipper!!.initialFromHistory(mMangaViewModel, mSettingViewModel, mUpdateConsumer)
//        }

        recyclerView.layoutManager = LinearLayoutManager(this, HORIZONTAL, false)

//        //To make it scroll smoothly
//        ViewCompat.setNestedScrollingEnabled(recyclerView, false)
        recyclerView.adapter = MangaPageItemAdapter(this,
                listOf(TitleAndUrl("1", "22"), TitleAndUrl("1", "22"), TitleAndUrl("1", "22")))
    }

    protected fun InitAd() {

        mAdView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                mAdView.visibility = View.GONE
                super.onAdFailedToLoad(errorCode)
            }
        }

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

    override fun IsCanBack(): Boolean {
        // TODO Auto-generated method stub
        return true
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

    override fun onDestroy() {
//        mViewFlipper!!.destroy()
        super.onDestroy()
    }

    companion object {

        val INTENT_EXTRA_FROM_HISTORY = "intent_extra_from_history"
    }
}
