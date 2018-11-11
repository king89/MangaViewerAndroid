package com.king.mangaviewer.ui.chapter

import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
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
import com.king.mangaviewer.util.MangaHelper
import com.king.mangaviewer.util.withViewModel
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.grantland.widget.AutofitTextView
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
    private val textView: AutofitTextView by lazy {
        findViewById<AutofitTextView>(R.id.textView)
    }

    private val progressBar: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }

    private val floatingActionButton: FloatingActionButton by lazy {
        findViewById<FloatingActionButton>(R.id.fab)
    }

    override fun initControl() {
        setContentView(R.layout.activity_manga_chapter)

        textView.text = this.appViewModel.Manga.selectedMangaMenuItem.title
        loadChapterCover()
        setupChapterList()
        initViewModel()

    }

    override fun onResume() {
        super.onResume()
        viewModel.updateHistoryChapter()
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
                    floatingActionButton.setImageResource(R.mipmap.ic_star_white)
                    floatingActionButton.setOnClickListener { view ->
                        this.removeFromFavorite()
                    }
                } else {
                    floatingActionButton.setImageResource(R.mipmap.ic_star_border_white)
                    floatingActionButton.setOnClickListener { view ->
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
            MangaHelper.getMenuCover(mangaViewModel.selectedMangaMenuItem)
        }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    imageView.setImageResource(R.color.manga_place_holder)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it: String ->
                    GlideApp.with(imageView)
                            .load(it)
                            .override(320, 320)
                            .placeholder(R.color.manga_place_holder)
                            .into(imageView)
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

}
