package com.king.mangaviewer.ui.chapter

import android.content.Intent
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaChapterItemAdapter

import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.ui.page.MangaPageActivityV2
import com.king.mangaviewer.adapter.MangaChapterItemAdapter.OnItemClickListener
import com.king.mangaviewer.adapter.MangaChapterItemWrapper
import com.king.mangaviewer.adapter.WrapperType.CATEGORY
import com.king.mangaviewer.adapter.WrapperType.CHAPTER
import com.king.mangaviewer.adapter.WrapperType.LAST_READ
import com.king.mangaviewer.di.GlideApp
import com.king.mangaviewer.model.MangaChapterItem
import com.king.mangaviewer.util.MangaHelper
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.grantland.widget.AutofitTextView

class MangaChapterActivity : BaseActivity(), OnItemClickListener {

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
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_manga_chapter)

        initFAB()
        textView.text = this.appViewModel.Manga.selectedMangaMenuItem.title
        loadChapterCover()

        progressBar.visibility = View.VISIBLE
        compositeDisposable.add(Flowable.fromCallable {

            val mList = mangaHelper.getChapterList(
                    mangaViewModel.selectedMangaMenuItem)
            mangaViewModel.mangaChapterList = mList

            val dataList = ArrayList<MangaChapterItemWrapper>()

            val lastReadItem = appViewModel.HistoryManga.getLastRead(
                    mangaViewModel.selectedMangaMenuItem)
            val historyItem = appViewModel.HistoryManga.getHistoryChapterList()
            lastReadItem?.run {
                dataList.add(
                        MangaChapterItemWrapper(getString(R.string.chapter_last_read), CATEGORY,
                                null))
                dataList.add(MangaChapterItemWrapper(title, LAST_READ, this))
            }
            mList?.run {
                dataList.add(
                        MangaChapterItemWrapper(getString(R.string.chapter_list), CATEGORY, null))
                forEach {
                    dataList.add(MangaChapterItemWrapper(it.title, CHAPTER, it,
                            historyItem.any { history ->
                                history.hash == it.hash
                            }
                    ))
                }

            }

            dataList
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLoading() }
                .doOnTerminate { hideLoading() }
                .subscribe {
                    setupChapterList(it)
                    updateChapterCount()
                })
    }

    private fun loadChapterCover() {
        //get the first page image in the latest dataList
        Single.fromCallable {
            MangaHelper.getMenuCover(this, mangaViewModel.selectedMangaMenuItem)
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

    private fun initFAB() {
        if (appViewModel.Setting.checkIsFavourited(appViewModel.Manga.selectedMangaMenuItem)) {
            floatingActionButton.setImageResource(R.mipmap.ic_star_white)
        } else {
            floatingActionButton.setImageResource(R.mipmap.ic_star_border_white)
        }

        floatingActionButton.setOnClickListener {
            //try to add, if yes, set favourited, if not, remove it from favourite list
            val chapterCount = if (mangaViewModel.mangaChapterList == null) 0 else mangaViewModel.mangaChapterList.size
            if (appViewModel.Setting.addFavouriteManga(appViewModel.Manga.selectedMangaMenuItem,
                            chapterCount)) {
                floatingActionButton.setImageResource(R.mipmap.ic_star_white)
                Toast.makeText(this@MangaChapterActivity, getString(R.string.favourited),
                        Toast.LENGTH_SHORT).show()
            } else {
                appViewModel.Setting.removeFavouriteManga(appViewModel.Manga.selectedMangaMenuItem)
                floatingActionButton.setImageResource(R.mipmap.ic_star_border_white)
                Toast.makeText(this@MangaChapterActivity, getString(R.string.unfavourited),
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(chapter: MangaChapterItem) {
        // TODO Auto-generated method stub
        mangaViewModel.selectedMangaChapterItem = chapter
        startActivity(Intent(this, MangaPageActivityV2::class.java))
        overridePendingTransition(R.anim.in_rightleft,
                R.anim.out_rightleft)
    }

    override fun update(msg: Message?) {
        // TODO Auto-generated method stub

    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE

    }

    private fun updateChapterCount() {
        val chapterCount = if (mangaViewModel.mangaChapterList == null) 0 else mangaViewModel.mangaChapterList.size
        val menu = appViewModel.Manga.selectedMangaMenuItem
        appViewModel.Setting.removeFavouriteManga(menu)
        appViewModel.Setting.addFavouriteManga(menu, chapterCount)
    }

    private fun setupChapterList(dataList: List<MangaChapterItemWrapper>) {
        val adapter = MangaChapterItemAdapter(this,
                this,
                dataList)

        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter
    }

    override fun getActionBarTitle(): String {
        // TODO Auto-generated method stub
        return this.appViewModel.Manga.selectedMangaMenuItem.title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chapter_menu, menu)


        return super.onCreateOptionsMenu(menu)
    }

}
