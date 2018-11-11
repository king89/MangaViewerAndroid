package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.OnItemClickListener
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.component.MangaGridView
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.withViewModel
import dagger.android.support.AndroidSupportInjection

open class HomeFragment : BaseFragment() {


    lateinit var viewModel: HomeFragmentViewModel

    lateinit var gv: MangaGridView
    lateinit var tvMangaSource: TextView
    lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

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
        val tv = rootView.findViewById<View>(R.id.textView) as TextView
        mSwipeRefreshLayout = rootView.findViewById<View>(
                R.id.swipeRefreshLayout) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener { refresh() }
        gv = rootView.findViewById<View>(R.id.gridView) as MangaGridView
        gv.adapter = MangaMenuItemAdapter(object : OnItemClickListener {
            override fun onClick(menu: MangaMenuItem) {
                viewModel.selectMangaMenu(menu)
                startActivity(Intent(context, MangaChapterActivity::class.java))
                activity!!.overridePendingTransition(R.anim.in_rightleft,
                        R.anim.out_rightleft)
            }
        })
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

            this.attachToView()
        }
    }

    open val TAG = "HomeFragment"

}