package com.king.mangaviewer.ui.main.local

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.king.mangaviewer.R
import com.king.mangaviewer.R.integer
import com.king.mangaviewer.adapter.LocalMangaMenuItemAdapter
import com.king.mangaviewer.adapter.LocalMangaMenuItemAdapter.OnSelectedChangeListener
import com.king.mangaviewer.adapter.MangaMenuItemAdapter
import com.king.mangaviewer.adapter.MangaMenuItemAdapter.MangaMenuAdapterListener
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.model.MangaMenuItem
import com.king.mangaviewer.ui.main.HasFloatActionButton
import com.king.mangaviewer.ui.main.local.AddLocalFragment.OnAddLocalMangaCallback
import com.king.mangaviewer.util.AppNavigator
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.withViewModel
import javax.inject.Inject

class LocalFragment : BaseFragment(), HasFloatActionButton {
    private lateinit var mRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var localMangaMenuItemAdapter: LocalMangaMenuItemAdapter
    private lateinit var gridLayoutManager: androidx.recyclerview.widget.GridLayoutManager
    private lateinit var mSwipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var tv: TextView
    private lateinit var fab: FloatingActionButton
    lateinit var viewModel: LocalFragmentViewModel

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_local, container, false)

        mRecyclerView = rootView.findViewById<View>(
            R.id.viewPager
        ) as androidx.recyclerview.widget.RecyclerView
        gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(activity,
            resources.getInteger(integer.gridvivew_column_num))
        mRecyclerView.layoutManager = gridLayoutManager
        localMangaMenuItemAdapter = LocalMangaMenuItemAdapter(object : MangaMenuAdapterListener {
            override fun onItemClicked(view: View, item: MangaMenuItem) {
                viewModel.selectMangaMenu(item)
                appNavigator.navigateToChapter(Pair(view, "cover"))
            }
        }, object : OnSelectedChangeListener {
            override fun onChange(menuList: List<MangaMenuItem>) {
                fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_remove))
                viewModel.selectedLocalMenu.postValue(menuList)
            }
        })
        mRecyclerView.adapter = localMangaMenuItemAdapter

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
        withViewModel<LocalFragmentViewModel>(fragmentViewModelFactory) {
            viewModel = this
            this.loadingState.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Loading -> {
                        mSwipeRefreshLayout.isRefreshing = true
                    }

                    is Idle -> {
                        mSwipeRefreshLayout.isRefreshing = false
                    }
                }
            })

            this.mangaList.observe(viewLifecycleOwner, Observer {
                Logger.d(TAG, "manga list observe, size: ${it!!.size}")
                if (it!!.isEmpty()) {
                    tv.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.GONE
                }
                (mRecyclerView.adapter as? MangaMenuItemAdapter)?.submitList(it)

            })

            this.attachToView()
        }

    }

    override fun initFab(fab: FloatingActionButton) {
        this.fab = fab
        fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_add))
        fab.show()
    }

    override fun onClick() {
        if (localMangaMenuItemAdapter.selectableMode) {
            viewModel.removeLocalMenu()
            localMangaMenuItemAdapter.toggleSelectableMode()
            fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_add))
        } else {
            AddLocalFragment().apply {
                setCallback(object : OnAddLocalMangaCallback {
                    override fun onAdded() {
                        Logger.d(TAG, "Added local manga, refreshing")
                        viewModel.refresh(true)
                    }
                })
            }.show(this.parentFragmentManager, "")
        }
    }

    companion object {
        const val TAG = "LocalFragment"
    }
}