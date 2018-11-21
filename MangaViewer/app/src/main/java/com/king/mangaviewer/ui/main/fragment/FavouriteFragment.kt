package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.util.Pair
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.FavouriteMangaItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import com.king.mangaviewer.util.AppNavigator
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.withViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FavouriteFragment : BaseFragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var tv: TextView

    lateinit var viewModel: FavouriteFragmentViewModel

    @Inject
    @field:FragmentScopedFactory
    lateinit var fragmentViewModelFactory: ViewModelFactory

    @Inject
    lateinit var appNavigator: AppNavigator

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    init {
        this.setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_favourite, container, false)
        mRecyclerView = rootView.findViewById<View>(R.id.viewPager) as RecyclerView
        gridLayoutManager = GridLayoutManager(activity,
                resources.getInteger(R.integer.gridvivew_column_num))
        mRecyclerView.layoutManager = gridLayoutManager
        mRecyclerView.adapter = FavouriteMangaItemAdapter { view, item ->
            viewModel.selectMangaMenu(item)
            appNavigator.navigateToChapter(Pair(view, "cover"))

        }
        tv = rootView.findViewById<View>(R.id.textView) as TextView
        mSwipeRefreshLayout = rootView.findViewById<View>(
                R.id.swipeRefreshLayout) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh(false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    private fun initViewModel() {
        withViewModel<FavouriteFragmentViewModel>(fragmentViewModelFactory) {
            viewModel = this
            this.loadingState.observe(this@FavouriteFragment, Observer {
                when (it) {
                    is Loading -> {
                        mSwipeRefreshLayout.isRefreshing = true
                    }
                    is Idle -> {
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            })

            this.mangaList.observe(this@FavouriteFragment, Observer {
                Logger.d(TAG, "manga list observe, size: ${it!!.size}")
                if (it!!.isEmpty()) {
                    tv.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.GONE
                }
                (mRecyclerView.adapter as? FavouriteMangaItemAdapter)?.submitList(it)

            })

            this.attachToView()
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        gridLayoutManager.spanCount = resources.getInteger(R.integer.gridvivew_column_num)
    }

    companion object {
        const val TAG = "FavouriteFragment"
    }
}
