package com.king.mangaviewer.ui.main.fragment

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.RIGHT
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.HistoryChapterItemAdapter
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.ui.chapter.MangaChapterActivity
import com.king.mangaviewer.ui.main.HasFloatActionButton
import com.king.mangaviewer.ui.page.MangaPageActivityV2
import com.king.mangaviewer.ui.page.MangaPageActivityV2.Companion.INTENT_EXTRA_FROM_HISTORY
import com.king.mangaviewer.util.AppNavigator
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.RecyclerItemTouchHelper
import com.king.mangaviewer.util.withViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class HistoryFragment : BaseFragment(), HasFloatActionButton {

    private var recyclerView: RecyclerView? = null
    private var adapter: HistoryChapterItemAdapter? = null
        get() = recyclerView?.adapter as? HistoryChapterItemAdapter
    lateinit var tv: TextView
    lateinit var viewModel: HistoryFragmentViewModel
    private var fab: FloatingActionButton? = null

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
        inflater!!.inflate(R.menu.history_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.menu_delete) {
            AlertDialog.Builder(this.activity!!)
                    .setTitle(getString(R.string.msg_history_dialog_title))
                    .setMessage(getString(R.string.msg_history_dialog_message))
                    .setPositiveButton(getString(R.string.clear)) { dialog, which ->
                        viewModel.clearAllHistory()
                    }
                    .setNegativeButton(getString(R.string.cancel), null)
                    .show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = rootView.findViewById<View>(R.id.listView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        tv = rootView.findViewById<View>(R.id.textView) as TextView
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView!!.adapter = HistoryChapterItemAdapter(
                activity as Context) { imageView, item, showAsChapter ->
            if (showAsChapter) {
                viewModel.selectChapter(item)
                val intent = Intent(context, MangaPageActivityV2::class.java)
                intent.putExtra(INTENT_EXTRA_FROM_HISTORY, true)
                this@HistoryFragment.startActivity(intent)
                this@HistoryFragment.activity?.overridePendingTransition(R.anim.in_rightleft,
                        R.anim.out_rightleft)
            } else {
                viewModel.selectMenu(item)
                appNavigator.navigateToChapter(Pair(imageView, "cover"))

            }
        }

        addItemTouchForRecyclerView()

        initViewModel()
    }

    private fun addItemTouchForRecyclerView() {
        val itemTouchHelperCallback = RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT) { viewHolder, direction, position ->
            if (adapter!!.showAsChapter) {
                viewModel.deleteChapter(adapter!!.getItemByPos(viewHolder.adapterPosition))
            } else {
                viewModel.deleteMenu(adapter!!.getItemByPos(viewHolder.adapterPosition))
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)

    }

    private fun initViewModel() {
        withViewModel<HistoryFragmentViewModel>(fragmentViewModelFactory) {
            viewModel = this
            loadingState.observe(this@HistoryFragment, Observer {
                when (it) {
                    Loading -> showLoading()
                    Idle -> hideLoading()
                }
            })

            mangaList.observe(this@HistoryFragment, Observer {
                if (it!!.isEmpty()) {
                    tv.visibility = View.VISIBLE
                } else {
                    tv.visibility = View.GONE
                }
                adapter?.submitList(it)
            })

            attachToView()
        }
    }

    private fun hideLoading() {
    }

    private fun showLoading() {
    }

    override fun initFab(fab: FloatingActionButton) {
        this.fab = fab
        fab.setImageDrawable(ContextCompat.getDrawable(fab.context, R.drawable.ic_unfold))
        fab.show()
    }

    override fun onClick() {
        //toggle list type
        adapter?.apply {
            Logger.d(TAG, "showAsChapter 1: $showAsChapter")
            changeShowType(!showAsChapter)
            Logger.d(TAG, "showAsChapter 2: $showAsChapter")

            if (showAsChapter) {
                fab?.setImageDrawable(
                        ContextCompat.getDrawable(fab?.context!!, R.drawable.ic_unfold))
            } else {
                fab?.setImageDrawable(ContextCompat.getDrawable(fab?.context!!, R.drawable.ic_fold))
            }
            submitList(viewModel.mangaList.value!!)
        }

    }
    companion object {
        const val TAG = "HistoryFragment"
    }

}