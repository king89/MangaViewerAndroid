package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.base.ErrorMessage.NoError
import com.king.mangaviewer.component.MangaGridView
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import com.king.mangaviewer.ui.main.HasFloatActionButton
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.VersionUtil
import com.king.mangaviewer.util.withViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_manga_gridview.layout_error

open class HomeFragment : BaseFragment(), HasFloatActionButton {

    lateinit var viewModel: HomeFragmentViewModel

    lateinit var gv: MangaGridView
    lateinit var tvMangaSource: TextView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private var snackbar: Snackbar? = null
    private var rootView: View? = null
    private var fab: FloatingActionButton? = null

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun refresh() {
        this.mangaViewModel.mangaMenuList = null
        tvMangaSource.text = settingViewModel.selectedWebSource.displayName
        viewModel.getData()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return activity!!.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_manga_gridview, container, false)
        tvMangaSource = rootView.findViewById<View>(R.id.manga_source_textView) as TextView
        val selectedMangaSourceName = settingViewModel.selectedWebSource.displayName
        tvMangaSource.text = selectedMangaSourceName
        mSwipeRefreshLayout = rootView.findViewById<View>(
                R.id.swipeRefreshLayout) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener { refresh() }

        gv = rootView.findViewById<View>(R.id.gridView) as MangaGridView
        gv.adapter = MangaMenuItemAdapter { view, item ->
            viewModel.selectMangaMenu(item)
            val intent = Intent(context, MangaChapterActivity::class.java)
            if (VersionUtil.isGreaterOrEqualApi21()) {
                val statusBar = activity!!.findViewById<View>(android.R.id.statusBarBackground)
                val navigationBar = activity!!.findViewById<View>(android.R.id.navigationBarBackground)
                val appBarLayout = activity!!.findViewById<View>(R.id.appBarLayout)
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this.activity!!,
                        Pair.create(navigationBar,
                                Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME),
                        Pair(view, "cover"))
                startActivity(intent, options.toBundle())
            }else{
                startActivity(intent)
                activity!!.overridePendingTransition(R.anim.in_rightleft,
                        R.anim.out_rightleft)
            }
        }
        gv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && fab?.isShown == true)
                    fab?.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab?.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        rootView.findViewById<Button>(R.id.btRetry).setOnClickListener {
            refresh()
        }
        this.rootView = rootView
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.d(TAG, "onViewCreated")

        //bind view model
        withViewModel<HomeFragmentViewModel>(fragmentViewModelFactory) {
            viewModel = this
            this.loadingState.observe(this@HomeFragment, Observer {
                when (it) {
                    is Loading -> {
                        mSwipeRefreshLayout.isRefreshing = true
                        Logger.d(TAG, "start refreshing")
                    }
                    is Idle -> {
                        mSwipeRefreshLayout.isRefreshing = false
                        Logger.d(TAG, "stop refreshing")
                    }
                }
            })

            this.mangaList.observe(this@HomeFragment, Observer {
                //update adapter
                it?.run {
                    (gv.adapter as MangaMenuItemAdapter).submitList(it)
                }
            })

            errorMessage.observe(this@HomeFragment, Observer { it ->
                when (it!!) {
                    NoError -> {
                        gv.visibility = VISIBLE
                        layout_error.visibility = GONE
                    }
                    else -> {
                        gv.visibility = GONE
                        layout_error.visibility = VISIBLE
                    }
                }

            })
            this.attachToView()
        }
    }

    override fun initFab(fab: FloatingActionButton) {
//        this.fab = fab
//        fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_search))
//        fab.show()
        fab.hide()
    }

    override fun onClick() {
    }

    open val TAG = "HomeFragment"

}