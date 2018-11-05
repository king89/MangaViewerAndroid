package com.king.mangaviewer.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView

import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.OnItemClickListener
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider
import com.king.mangaviewer.component.MangaGridView
import com.king.mangaviewer.model.MangaWebSource
import com.king.mangaviewer.util.MangaHelper
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.Logger
import io.reactivex.Completable

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by KinG on 7/23/2015.
 */
class SearchResultActivity : BaseActivity() {

    lateinit var gv: MangaGridView
    internal var state = HashMap<String, Any>()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var searchView: SearchView? = null
    private var queryString = ""
    lateinit var mangaSourceTv: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
    }

    override fun initControl() {
        setContentView(R.layout.activity_search_result)
        gv = this.findViewById<View>(R.id.gridView) as MangaGridView
        swipeRefreshLayout = this.findViewById<View>(R.id.swipeRefreshLayout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            setSearchResult(queryString)
            swipeRefreshLayout.isRefreshing = false
        }
        mangaSourceTv = this.findViewById<View>(R.id.manga_source_textView) as TextView
        val tv = findViewById<View>(R.id.textView) as TextView
        gv = findViewById<View>(R.id.gridView) as MangaGridView
        gv.adapter = MangaMenuItemAdapter(object:OnItemClickListener{
            override fun onClick(menu: MangaMenuItem) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView!!.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))
        return true
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH == intent.action) {
            queryString = intent.getStringExtra(SearchManager.QUERY)
            //use the query to search
            this.supportActionBar!!.title = queryString
            getQueryResult(queryString)
        }
        searchView?.onActionViewCollapsed()

    }

    private fun getQueryResult(query: String) {

        setSearchResult(query)

    }

    private fun setSearchResult(query: String) {
        mangaSourceTv.text = appViewModel.Setting.selectedWebSource.displayName

        //reset all manga list
        this.appViewModel.Manga.resetAllMangaList()
        this.appViewModel.Manga.allMangaStateHash[MangaProvider.STATE_SEARCH_QUERYTEXT] = query
        val hash = this.appViewModel.Manga.allMangaStateHash

        Single.fromCallable {
            val list = MangaHelper(this).getSearchMangeList(mutableListOf(), hash)
            list
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    (gv.adapter as? MangaMenuItemAdapter)?.submitList(it)
                }, {
                    Logger.e(TAG, it)
                })
                .apply { compositeDisposable.add(this) }
    }

    private fun displayMangaSource(anchorView: View) {
        val popup = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.menu_main_setting, null)

        val lv = layout.findViewById<View>(R.id.listView) as ListView

        val mws = appViewModel.Setting.mangaWebSources
        var tSelectWebSourcePos = 0
        val source = ArrayList<String>()
        if (mws != null) {
            for ((i, m) in mws.withIndex()) {
                source.add(m.displayName)
                if (m.id == appViewModel.Setting.selectedWebSource.id) {
                    tSelectWebSourcePos = i
                }
            }
        }

        lv.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, source)
        lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            appViewModel.Setting.setSelectedWebSource(mws!![position],
                    this@SearchResultActivity)
            appViewModel.Manga.mangaMenuList = null
            setSearchResult(queryString)
            popup.dismiss()
        }
        lv.setItemChecked(tSelectWebSourcePos, true)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_setting -> {
                val v = findViewById<View>(R.id.menu_setting)
                displayMangaSource(v)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "SearchResultActivity"
    }
}