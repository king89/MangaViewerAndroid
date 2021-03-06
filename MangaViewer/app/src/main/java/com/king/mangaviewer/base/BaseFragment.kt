package com.king.mangaviewer.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.king.mangaviewer.R
import com.king.mangaviewer.viewmodel.HistoryViewModel
import com.king.mangaviewer.viewmodel.MangaViewModel
import com.king.mangaviewer.viewmodel.SettingViewModel
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * A simple [Fragment] subclass.
 */
open class BaseFragment : androidx.fragment.app.Fragment() {

    protected val disposable = CompositeDisposable()

    protected val settingViewModel: SettingViewModel
        get() = (this.activity as BaseActivity).appViewModel.Setting

    protected val mangaViewModel: MangaViewModel
        get() = (this.activity as BaseActivity).appViewModel.Manga

    protected val historyViewModel: HistoryViewModel
        get() = (this.activity as BaseActivity).appViewModel.HistoryManga

    protected open fun getContentBackground() {
    }

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    protected open fun startAsyncTask() {
        Observable.fromCallable {
            getContentBackground()
            1
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { updateContent() }
                .apply { disposable.add(this) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        val textView = TextView(activity)
        textView.setText(R.string.hello_blank_fragment)

        return textView
    }

    open fun refresh() {}

    protected open fun updateContent() {}

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

}// Required empty public constructor
