package com.king.mangaviewer.ui.main.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.HistoryChapterItemAdapter
import com.king.mangaviewer.base.BaseFragment
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import com.king.mangaviewer.model.LoadingState.Idle
import com.king.mangaviewer.model.LoadingState.Loading
import com.king.mangaviewer.ui.page.MangaPageActivityV2
import com.king.mangaviewer.ui.page.MangaPageActivityV2.Companion.INTENT_EXTRA_FROM_HISTORY
import com.king.mangaviewer.util.AppNavigator
import com.king.mangaviewer.util.RecyclerItemTouchHelper
import com.king.mangaviewer.util.withViewModel
import javax.inject.Inject

class HistoryFragment : BaseFragment() {

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
        recyclerView = rootView.findViewById<View>(
            R.id.listView) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        tv = rootView.findViewById<View>(R.id.textView) as TextView
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView!!.adapter = HistoryChapterItemAdapter(
                activity as Context,
                { imageView, item ->
                    viewModel.selectMenu(item)
                    appNavigator.navigateToChapter(Pair(imageView, "cover"))
                },
                { _, item ->
                    viewModel.selectChapter(item)
                    val intent = Intent(context, MangaPageActivityV2::class.java)
                    intent.putExtra(INTENT_EXTRA_FROM_HISTORY, true)
                    this@HistoryFragment.startActivity(intent)
                    this@HistoryFragment.activity?.overridePendingTransition(R.anim.in_rightleft,
                            R.anim.out_rightleft)
                })

        addItemTouchForRecyclerView()

        initViewModel()
    }

    private fun addItemTouchForRecyclerView() {
        val itemTouchHelperCallback = RecyclerItemTouchHelper(0,
                ItemTouchHelper.LEFT) { viewHolder, direction, position ->
            val item = adapter!!.getItemByPos(viewHolder.adapterPosition)
            val maxLength = 20
            val itemTitle = if (item.menu.title.length > maxLength) {
                item.menu.title.substring(0..20) + "..."
            } else {
                item.menu.title
            }
            val snackbar = Snackbar.make(this.view!!,
                    getString(R.string.history_item_removed, itemTitle), LENGTH_LONG)
            var undo = false
            snackbar.setAction(getString(R.string.undo)) {
                undo = true
                adapter!!.notifyItemChanged(viewHolder.adapterPosition)
            }
            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (!undo) {
                        viewModel.deleteMenu(item)
                    }
                }
            })
            snackbar.show()
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

    companion object {
        const val TAG = "HistoryFragment"
    }

}