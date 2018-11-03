package com.king.mangaviewer.util

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentActivity
import com.king.mangaviewer.base.BaseFragment

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(
        viewModelFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

inline fun <reified T : ViewModel> BaseFragment.getViewModel(
        viewModelFactory: ViewModelProvider.Factory): T {
    return ViewModelProviders.of(this, viewModelFactory)[T::class.java]
}

inline fun <reified T : ViewModel> FragmentActivity.withViewModel(
        viewModelFactory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = getViewModel<T>(viewModelFactory)
    vm.body()
    return vm
}

inline fun <reified T : ViewModel> BaseFragment.withViewModel(
        viewModelFactory: ViewModelProvider.Factory, body: T.() -> Unit): T {
    val vm = getViewModel<T>(viewModelFactory)
    vm.body()
    return vm
}