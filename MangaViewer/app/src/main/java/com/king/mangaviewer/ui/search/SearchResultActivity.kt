package com.king.mangaviewer.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.MangaMenuAdapterListener
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.component.MangaGridView
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import com.king.mangaviewer.util.withViewModel
import java.util.ArrayList
import java.util.HashMap
import javax.inject.Inject

/**
 * Created by KinG on 7/23/2015.
 */
class SearchResultActivity : BaseActivity() {

    @Inject
    @field:ActivityScopedFactory
    lateinit var activityScopedFactory: ViewModelFactory
    lateinit var viewModel: SearchResultActivityViewModel

    lateinit var gv: MangaGridView
    internal var state = HashMap<String, Any>()
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var searchView: SearchView? = null
    private var queryString = ""
    lateinit var mangaSourceTv: TextView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIntent(intent)
    }

    override fun initControl() {
        setContentView(R.layout.activity_search_result)
        initViewModel()

        gv = this.findViewById<View>(R.id.gridView) as MangaGridView
        mSwipeRefreshLayout = this.findViewById<View>(
            R.id.swipeRefreshLayout) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener {
            getQueryResult(queryString)
            mSwipeRefreshLayout.isRefreshing = false
        }
        mangaSourceTv = this.findViewById<View>(R.id.manga_source_textView) as TextView
        gv = findViewById<View>(R.id.gridView) as MangaGridView
        gv.adapter = MangaMenuItemAdapter(object : MangaMenuAdapterListener {
            override fun onItemClicked(view: View, item: MangaMenuItem) {
                viewModel.selectMenu(item)
                startActivity(Intent(this@SearchResultActivity, MangaChapterActivity::class.java))
                this@SearchResultActivity.overridePendingTransition(R.anim.in_rightleft,
                    R.anim.out_rightleft)
            }
        })
    }

    private fun initViewModel() {
        withViewModel<SearchResultActivityViewModel>(activityScopedFactory) {
            viewModel = this
            this.loadingState.observe(this@SearchResultActivity, Observer {
                when (it) {
                    is Loading -> {
                        mSwipeRefreshLayout.isRefreshing = true
                    }
                    is Idle -> {
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            })

            this.mangaList.observe(this@SearchResultActivity, Observer {
                //update adapter
                it?.run {
                    (gv.adapter as MangaMenuItemAdapter).submitList(it)
                }
            })
        }
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
        mangaSourceTv.text = appViewModel.Setting.selectedWebSource.displayName
        viewModel.searchManga(query)
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
            getQueryResult(queryString)
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