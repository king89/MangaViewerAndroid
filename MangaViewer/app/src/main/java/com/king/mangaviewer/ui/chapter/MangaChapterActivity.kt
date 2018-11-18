package com.king.mangaviewer.ui.chapter

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.OnItemClickListener
import com.king.mangaviewer.adapter.MangaChapterItemWrapper
import com.king.mangaviewer.adapter.WrapperType.CATEGORY
import com.king.mangaviewer.adapter.WrapperType.CHAPTER
import com.king.mangaviewer.adapter.WrapperType.LAST_READ
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.ui.page.MangaPageActivityV2
import com.king.mangaviewer.util.MangaHelperV2
import com.king.mangaviewer.util.VersionUtil
import com.king.mangaviewer.util.VersionUtil.isGreaterOrEqualApi19
import com.king.mangaviewer.util.glide.BlurTransformation
import com.king.mangaviewer.util.withViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_manga_chapter.fabShare
import kotlinx.android.synthetic.main.activity_manga_chapter.fabSort
import kotlinx.android.synthetic.main.activity_manga_chapter.ivCover
import kotlinx.android.synthetic.main.activity_manga_chapter.tvTitle
import javax.inject.Inject

class MangaChapterActivity : BaseActivity(), OnItemClickListener {

    @Inject
    @field:ActivityScopedFactory
    lateinit var activityScopedFactory: ViewModelFactory
    lateinit var viewModel: MangaChapterActivityViewModel

    private val listView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.viewPager)
    }
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
        loadChapterCover()
        setupChapterList()
        initButtons()
        initViewModel()

    }

    override fun onResume() {
        super.onResume()
        viewModel.updateHistoryChapter()
    }

    private fun initButtons() {
        fabShare.setOnClickListener { }
        fabSort.setOnClickListener { }
    }

    private fun initViewModel() {
        progressBar.visibility = View.VISIBLE
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

            this.chapterPair.observe(this@MangaChapterActivity, Observer { chapterPair ->
                compositeDisposable.add(Flowable.fromCallable {

                    val mList = chapterPair!!.first
                    val historyItem = chapterPair.second
                    val dataList = ArrayList<MangaChapterItemWrapper>()

                    val lastReadItem = historyItem.firstOrNull()

                    lastReadItem?.run {
                        dataList.add(
                                MangaChapterItemWrapper(getString(R.string.chapter_last_read),
                                        CATEGORY,
                                        null))
                        dataList.add(MangaChapterItemWrapper(title, LAST_READ, this))
                    }
                    mList.run {
                        if (mList.isEmpty()) return@run
                        dataList.add(
                                MangaChapterItemWrapper(getString(R.string.chapter_list), CATEGORY,
                                        null))
                        forEach {
                            dataList.add(MangaChapterItemWrapper(it.title, CHAPTER, it,
                                    historyItem.any { history ->
                                        history.hash == it.hash
                                    }
                            ))
                        }

                    }

                    dataList
                }.subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            (listView.adapter as? MangaChapterItemAdapter)?.submitList(it)
                        })
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
        //get the first page image in the latest dataList
        Single.fromCallable {
            MangaHelperV2.getMenuCover(mangaViewModel.selectedMangaMenuItem)
        }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    imageView.setImageResource(R.color.manga_place_holder)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: String ->
                    val glide = GlideApp.with(imageView)
                            .asBitmap()
                            .load(it)
                            .override(320, 320)
                            .placeholder(R.color.manga_place_holder)
                    glide.into(ivCover)

                    if (isGreaterOrEqualApi19()) {
                        glide.transform(BlurTransformation(imageView.context))
                    }
                    glide.into(imageView)

                }
                .apply { compositeDisposable.add(this) }

    }

    override fun onClick(chapter: MangaChapterItem) {
        viewModel.selectChapter(chapter)
        startActivity(Intent(this, MangaPageActivityV2::class.java))
        overridePendingTransition(R.anim.in_rightleft,
                R.anim.out_rightleft)
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE

    }

    private fun setupChapterList() {
        val adapter = MangaChapterItemAdapter(this,
                this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter
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
