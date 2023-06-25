package com.king.mangaviewer.ui.chapter

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.OnItemClickListener
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.OnSelectedChangeListener
import com.king.mangaviewer.adapter.MangaChapterStateItem
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
import com.king.mangaviewer.model.HistoryMangaChapterItem
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.ui.page.MangaPageActivityV2
import com.king.mangaviewer.util.GlideImageHelper
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.VersionUtil
import com.king.mangaviewer.util.glide.BlurTransformation
import com.king.mangaviewer.util.glide.CropImageTransformation
import com.king.mangaviewer.util.withViewModel
import javax.inject.Inject

class MangaChapterActivity : BaseActivity() {

    @Inject
    @field:ActivityScopedFactory
    lateinit var activityScopedFactory: ViewModelFactory
    var viewModel: MangaChapterActivityViewModel? = null
    lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val btDownload: ImageButton by lazy { findViewById(R.id.btDownload) }
    private val fabShare: FloatingActionButton by lazy { findViewById(R.id.fabShare) }
    private val fabSort: FloatingActionButton by lazy { findViewById(R.id.fabSort) }
    private val groupLastRead: Group by lazy { findViewById(R.id.groupLastRead) }
    private val ivCover: ImageView by lazy { findViewById(R.id.ivCover) }
    private val rvChapterList: RecyclerView by lazy { findViewById(R.id.rvChapterList) }
    private val rvLastRead: RecyclerView by lazy { findViewById(R.id.rvLastRead) }
    private val swipeRefreshLayout: SwipeRefreshLayout by lazy { findViewById(R.id.swipeRefreshLayout) }
    private val tvLastRead: TextView by lazy { findViewById(R.id.tvLastRead) }
    private val tvTitle: TextView by lazy { findViewById(R.id.tvTitle) }
    private val bsDownload: LinearLayout by lazy { findViewById(R.id.bsDownload) }
    private val btCancel: Button by lazy { findViewById(R.id.btCancel) }
    private val btStartDownload: Button by lazy { findViewById(R.id.btStartDownload) }
    private val tvSelectedCount: TextView by lazy { findViewById(R.id.tvSelectedCount) }

    private val imageView: ImageView by lazy {
        findViewById<ImageView>(R.id.imageView)
    }

    private val progressBar: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }

    private val fabFavorite: FloatingActionButton by lazy {
        findViewById<FloatingActionButton>(R.id.fabFavorite)
    }

    override fun initControl() {
        setContentView(R.layout.activity_manga_chapter)

        tvTitle.text = this.appViewModel.Manga.selectedMangaMenuItem.title
//        ViewCompat.setNestedScrollingEnabled(rvChapterList, false)
//        ViewCompat.setNestedScrollingEnabled(rvLastRead, false)

        loadChapterCover()
        setupChapterList()
        initButtons()
        initBottomSheetDownload()

        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel?.getHistoryChapter()
    }


    private fun initButtons() {
        fabShare.setOnClickListener { }
        fabSort.setOnClickListener {
            viewModel?.sort()
            (rvChapterList.adapter as? MangaChapterItemAdapter)?.submitList(emptyList())
        }
        btDownload.setOnClickListener {
            toggleSelectableMode()
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel?.getChapterList()
            swipeRefreshLayout.isRefreshing = false
        }

    }

    private fun toggleSelectableMode() {
        (rvChapterList.adapter as? MangaChapterItemAdapter)?.toggleSelectableMode()
    }

    private fun initBottomSheetDownload() {
        bottomSheetBehavior = BottomSheetBehavior.from(bsDownload)
        bottomSheetBehavior.state = STATE_HIDDEN
        btCancel.setOnClickListener {
            toggleSelectableMode()
        }
        btStartDownload.setOnClickListener {
            Logger.d(
                TAG,
                "Selected item: ${viewModel?.selectedDownloadList?.value?.map { it.title }}"
            )
            viewModel?.startDownload()
            toggleSelectableMode()
        }
    }

    private fun updateBottomSheet(list: List<MangaChapterItem>) {
        val count = list.size
        if (count > 0) {
            bottomSheetBehavior.state = STATE_EXPANDED
            tvSelectedCount.text = resources.getQuantityString(
                R.plurals.item_selected, count,
                count
            )
        } else {
            bottomSheetBehavior.state = STATE_HIDDEN
        }
    }

    private fun initViewModel() {
        withViewModel<MangaChapterActivityViewModel>(activityScopedFactory) {
            viewModel = this
            this.loadingState.observe(this@MangaChapterActivity, Observer {
                when (it) {
                    is Loading -> {
                        showLoading()
                    }

                    is Idle -> {
                        hideLoading()
                    }
                }
            })

            this.chapterList.observe(this@MangaChapterActivity, Observer {
                (rvChapterList.adapter as? MangaChapterItemAdapter)?.submitList(it)
            })

            this.chapterHistoryList.observe(this@MangaChapterActivity, Observer {
                it?.run {
                    if (it.isEmpty()) return@run
                    (rvLastRead.adapter as? MangaChapterItemAdapter)?.apply {
                        val item = it.first()
                        submitList(listOf(item))
                        submitStateMap(
                            mapOf(
                                Pair(item.hash, MangaChapterStateItem(isRead = true))
                            )
                        )
                    }
                }
                tvLastRead.postDelayed({
                    if (it?.isNotEmpty() == true) {
                        groupLastRead.visibility = VISIBLE
                    } else {
                        groupLastRead.visibility = GONE
                    }
                }, 100)
            })

            this.chapterStateList.observe(this@MangaChapterActivity, Observer {
                it?.run {
                    (rvChapterList.adapter as? MangaChapterItemAdapter)?.submitStateMap(it)
                }
            })

            this.favouriteState.observe(this@MangaChapterActivity, Observer {
                if (it!!) {
                    fabFavorite.setImageResource(R.mipmap.ic_star_white)
                    fabFavorite.setOnClickListener { view ->
                        this.removeFromFavorite()
                    }
                } else {
                    fabFavorite.setImageResource(R.mipmap.ic_star_border_white)
                    fabFavorite.setOnClickListener { view ->
                        this.addToFavorite()
                    }
                }
            })

            this.selectedDownloadList.observe(this@MangaChapterActivity, Observer {
                updateBottomSheet(it!!)
            })

        }
        this.window.decorView.postDelayed({ viewModel?.attachToView() }, 500)

    }

    // TODO need to change
    private fun loadChapterCover() {
        val item = mangaViewModel.selectedMangaMenuItem

        val transformation = if (VersionUtil.isGreaterOrEqualApi19()) {
            BlurTransformation(imageView.context)
        } else {
            null
        }
        GlideImageHelper.getMenuCover(imageView, item, transformation)
            .subscribe()
            .apply { compositeDisposable.add(this) }

        GlideImageHelper.getMenuCover(ivCover, item, CropImageTransformation())
            .subscribe()
            .apply { compositeDisposable.add(this) }

    }

    private val onItemClickListener = object : OnItemClickListener {
        override fun onClick(chapter: MangaChapterItem) {
            viewModel?.selectChapter(chapter) {
                startActivity(Intent(this@MangaChapterActivity, MangaPageActivityV2::class.java))
                overridePendingTransition(
                    R.anim.in_rightleft,
                    R.anim.out_rightleft
                )
            }
        }
    }
    private val onHistoryItemClickListener = object : OnItemClickListener {
        override fun onClick(chapter: MangaChapterItem) {
            viewModel?.selectHistoryChapter(chapter as HistoryMangaChapterItem) {
                startActivity(Intent(this@MangaChapterActivity, MangaPageActivityV2::class.java))
                overridePendingTransition(
                    R.anim.in_rightleft,
                    R.anim.out_rightleft
                )
            }
        }
    }
    private val onSelectedChangeListener = object : OnSelectedChangeListener {
        override fun onChange(chapterList: List<MangaChapterItem>) {
            viewModel?.selectedDownloadList?.value = chapterList
        }
    }

    override fun showLoading() {
        progressBar.visibility = VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = GONE

    }

    private fun setupChapterList() {
        val adapter = MangaChapterItemAdapter(
            this,
            onItemClickListener, onSelectedChangeListener
        )
        rvChapterList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvChapterList.adapter = adapter

        val lastReadAdapter = MangaChapterItemAdapter(
            this,
            onHistoryItemClickListener
        )
        rvLastRead.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rvLastRead.adapter = lastReadAdapter
    }

    override fun getActionBarTitle(): String {
        return this.appViewModel.Manga.selectedMangaMenuItem.title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chapter_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (VersionUtil.isGreaterOrEqualApi21()) {
            supportFinishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val TAG = "MangaChapterActivity"
    }
}
