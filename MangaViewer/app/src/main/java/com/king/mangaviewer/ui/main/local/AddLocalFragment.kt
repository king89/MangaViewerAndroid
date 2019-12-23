package com.king.mangaviewer.ui.main.local


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.king.mangaviewer.R
import com.king.mangaviewer.adapter.LocalFileItemAdapter
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.FragmentScopedFactory
import com.king.mangaviewer.util.Logger
import com.king.mangaviewer.util.withViewModel
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FilenameFilter
import java.util.ArrayList
import java.util.Collections
import javax.inject.Inject


class AddLocalFragment : androidx.fragment.app.DialogFragment() {
    private val disposable = CompositeDisposable()

    // Stores names of traversed directories
    internal var str = ArrayList<String>()
    lateinit var adapter: LocalFileItemAdapter
    // Check if the first level of the directory structure is the one showing
    private var firstLvl: Boolean? = true
    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    private var fileList: MutableList<Item>? = null
    private var path: File? = null
    private var chosenFile: String = ""
    private var extraPath: String? = null
    private var listener: LocalFileItemAdapter.OnLocalFileItemClickListener? = null
    private var onAddCallback: OnAddLocalMangaCallback? = null
    private lateinit var tv: TextView

    @Inject
    @field:FragmentScopedFactory
    lateinit var fragmentViewModelFactory: ViewModelFactory

    private lateinit var viewModel: LocalFragmentViewModel

    init {
        this.setHasOptionsMenu(true)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog)
    }


    fun setCallback(callback: OnAddLocalMangaCallback) {
        onAddCallback = callback
    }

    override fun getDialog(): Dialog {
        val dialog = super.getDialog()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window.setLayout(width, height)
        }
        return dialog
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu!!.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        //TODO
        path = File(Environment.getExternalStorageDirectory().toString())
        val childFolders = extraPath?.split(
            "/".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray() ?: emptyArray()
        for (s in childFolders) {
            if (!s.isEmpty()) {
                str.add(s)
            }
        }
        val rootView = inflater.inflate(R.layout.fragment_add_local, container, false)
        tv = rootView.findViewById<View>(R.id.tvPath) as TextView
        val btCancel = rootView.findViewById<View>(R.id.btCancel) as Button
        btCancel.setOnClickListener {
            dismiss()
        }
        val btAdd = rootView.findViewById<View>(R.id.btAdd) as Button
        btAdd.setOnClickListener {
            val file = File(Environment.getExternalStorageDirectory(), extraPath)
            viewModel.addLocalMenu(file) {
                Logger.d(TAG, "Added local manga, finished")
                onAddCallback?.onAdded()
            }
            dismiss()
        }

        recyclerView = rootView.findViewById<View>(
            R.id.listView) as androidx.recyclerview.widget.RecyclerView
        recyclerView!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView!!.adapter = LocalFileItemAdapter(context, null, null)
        tv.text = extraPath
        //showDialog(DIALOG_LOAD_FILE);
        Log.d(TAG, path!!.absolutePath)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        RxPermissions(this).apply {
            request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe({ granted ->
                    if (granted) {
                        // All requested permissions are granted
                        startAsyncTask()
                    }

                }, { e ->
                    Logger.e(TAG, e)
                })

        }
    }

    private fun startAsyncTask() {
        Observable.fromCallable {
            getContentBackground()
            1
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { updateContent() }
            .apply { disposable.add(this) }
    }

    private fun initViewModel() {
        withViewModel<LocalFragmentViewModel>(fragmentViewModelFactory) {
            viewModel = this

        }
    }

    private fun getContentBackground() {
        loadFileList()
    }

    private fun updateContent() {
        listener = LocalFileItemAdapter.OnLocalFileItemClickListener { view, pos ->
            chosenFile = fileList!![pos].file
            val sel = File(path.toString() + "/" + chosenFile)
            if (sel.isDirectory) {
                firstLvl = false
                // Adds chosen directory to list
                str.add(chosenFile)
                fileList = null
                path = File(sel.toString() + "")

                getFolderPath()
                loadFileList()

                recyclerView!!.adapter = adapter

                Log.d(TAG, path!!.absolutePath)

            } else if (chosenFile!!.equals("up", ignoreCase = true) && !sel.exists()) {

                // present directory removed from list
                var s = ""
                if (str.size > 0) {
                    s = str.removeAt(str.size - 1)
                }
                // path modified to exclude present directory
                path = File(path!!.toString().substring(0,
                    path!!.toString().lastIndexOf(s)))
                fileList = null

                // if there are no more directories in the list, then
                // its the first level
                if (str.isEmpty()) {
                    firstLvl = true
                }
                getFolderPath()
                loadFileList()
                recyclerView!!.adapter = adapter
                Log.d(TAG, path!!.absolutePath)

            } else {
                // Perform action with file picked

            }// File picked
            // Checks if 'up' was clicked
        }

        adapter.setOnClickListener(listener)
        recyclerView!!.adapter = adapter
    }

    private fun getFolderPath() {
        extraPath = ""
        for (i in str.indices) {
            extraPath = extraPath + "/" + str[i]
        }
    }

    private fun loadFileList() {
        try {
            path!!.mkdirs()
        } catch (e: SecurityException) {
            Log.e(TAG, "unable to write on the sd card ")
        }

        // Checks whether path exists
        if (path!!.exists()) {
            val filter = FilenameFilter { dir, filename ->
                val sel = File(dir, filename)
                // Filters based on whether the file is hidden or not
                (sel.isFile && sel.name.contains(".zip") || sel.isDirectory) && !sel.isHidden
            }

            val fList = path!!.list(filter)
            fileList = ArrayList()
            if (fList != null) {
                for (i in fList.indices) {
                    fileList!!.add(Item(fList[i], R.mipmap.ic_collections_black))

                    // Convert into file path
                    val sel = File(path, fList[i])

                    // Set drawables
                    if (sel.isDirectory) {
                        fileList!![i].icon = R.mipmap.ic_folder_open_black
                        fileList!![i].fileType = 0
                        Log.d("DIRECTORY", fileList!![i].file)
                    } else {
                        Log.d("FILE", fileList!![i].file)
                    }
                }
            }
            Collections.sort(fileList!!)
            if (extraPath !== "") {
                fileList!!.add(0, Item("Up", R.mipmap.ic_publish_black))
            }
        } else {
            Log.e(TAG, "path does not exist")
        }

        if (fileList != null) {
            adapter = LocalFileItemAdapter(context, fileList, listener)
        }

        activity!!.runOnUiThread { tv.text = extraPath }


    }

    inner class Item(var file: String, var icon: Int?) : Comparable<Item> {
        var fileType: Int = 0 // 0 folder, 1 file

        init {
            this.fileType = 1
        }

        override fun toString(): String {
            return file
        }

        override fun compareTo(another: Item): Int {
            return if (this.fileType < another.fileType) {
                -1
            } else {
                this.file.compareTo(another.file)
            }
        }
    }

    interface OnAddLocalMangaCallback {
        fun onAdded()
    }

    companion object {

        private val TAG = "AddLocalFragment"
    }

}