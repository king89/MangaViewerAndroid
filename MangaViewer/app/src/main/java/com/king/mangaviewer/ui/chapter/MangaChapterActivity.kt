package com.king.mangaviewer.ui.chapter

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.OnItemClickListener
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.OnSelectedChangeListener
import com.king.mangaviewer.adapter.MangaChapterStateItem
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
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
import kotlinx.android.synthetic.main.activity_manga_chapter.btDownload
import kotlinx.android.synthetic.main.activity_manga_chapter.fabShare
import kotlinx.android.synthetic.main.activity_manga_chapter.fabSort
import kotlinx.android.synthetic.main.activity_manga_chapter.ivCover
import kotlinx.android.synthetic.main.activity_manga_chapter.rvChapterList
import kotlinx.android.synthetic.main.activity_manga_chapter.rvLastRead
import kotlinx.android.synthetic.main.activity_manga_chapter.tvLastRead
import kotlinx.android.synthetic.main.activity_manga_chapter.tvTitle
import javax.inject.Inject

class MangaChapterActivity : BaseActivity(), OnItemClickListener, OnSelectedChangeListener {

    @Inject
    @field:ActivityScopedFactory
    lateinit var activityScopedFactory: ViewModelFactory
    lateinit var viewModel: MangaChapterActivityViewModel


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
        ViewCompat.setNestedScrollingEnabled(rvChapterList, false)
        ViewCompat.setNestedScrollingEnabled(rvLastRead, false)

        loadChapterCover()
        setupChapterList()
        initButtons()
        initViewModel()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getHistoryChapter()
    }

    private fun initButtons() {
        fabShare.setOnClickListener { }
        fabSort.setOnClickListener {
            viewModel.sort()
            (rvChapterList.adapter as? MangaChapterItemAdapter)?.submitList(emptyList())
        }
        btDownload.setOnClickListener {
            (rvChapterList.adapter as? MangaChapterItemAdapter)?.toggleSelectableMode()
        }
    }

    private fun initViewModel() {
        progressBar.visibility = VISIBLE
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
                        submitStateMap(mapOf(
                            Pair(item.hash, MangaChapterStateItem(isRead = true))))
                    }
                }
                tvLastRead.postDelayed({
                    if (it?.isNotEmpty() == true) {
                        tvLastRead.visibility = VISIBLE
                    } else {
                        tvLastRead.visibility = GONE
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
            this.attachToView()
        }

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

    override fun onClick(chapter: MangaChapterItem) {
        viewModel.selectChapter(chapter) {
            startActivity(Intent(this, MangaPageActivityV2::class.java))
            overridePendingTransition(R.anim.in_rightleft,
                R.anim.out_rightleft)
        }
    }

    override fun onChange(chapterList: List<MangaChapterItem>) {
        Logger.d("-=-=", "chapterList: ${chapterList.map { it.title }}")
    }

    override fun showLoading() {
        progressBar.visibility = VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = GONE

    }

    private fun setupChapterList() {
        val adapter = MangaChapterItemAdapter(this,
            this, this)
        rvChapterList.layoutManager = LinearLayoutManager(this)
        rvChapterList.adapter = adapter

        val lastReadAdapter = MangaChapterItemAdapter(this,
            this)
        rvLastRead.layoutManager = LinearLayoutManager(this)
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
}
