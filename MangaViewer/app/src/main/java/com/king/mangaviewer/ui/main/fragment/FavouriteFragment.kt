package com.king.mangaviewer.ui.main.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.king.mangaviewer.R
import com.king.mangaviewer.R.integer
import com.king.mangaviewer.adapter.FavouriteMangaItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.MangaMenuAdapterListener
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.util.AppNavigator
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.withViewModel
import javax.inject.Inject

class FavouriteFragment : BaseFragment() {

    private lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var gridLayoutManager: androidx.recyclerview.widget.GridLayoutManager
    private lateinit var mSwipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var tv: TextView

    lateinit var viewModel: FavouriteFragmentViewModel

    @Inject
    @field:FragmentScopedFactory
    lateinit var fragmentViewModelFactory: ViewModelFactory

    @Inject
    lateinit var appNavigator: AppNavigator

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
        mRecyclerView = rootView.findViewById<View>(
            R.id.viewPager) as androidx.recyclerview.widget.RecyclerView
        gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(activity,
            resources.getInteger(integer.gridvivew_column_num))
        mRecyclerView.layoutManager = gridLayoutManager
        mRecyclerView.adapter = FavouriteMangaItemAdapter(object : MangaMenuAdapterListener {
            override fun onItemClicked(view: View, item: MangaMenuItem) {
                viewModel.selectMangaMenu(item)
                appNavigator.navigateToChapter(Pair(view, "cover"))
            }
        })

        tv = rootView.findViewById<View>(R.id.textView) as TextView
        mSwipeRefreshLayout = rootView.findViewById<View>(
            R.id.swipeRefreshLayout) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
