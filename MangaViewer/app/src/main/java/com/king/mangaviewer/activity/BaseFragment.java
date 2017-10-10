package com.king.mangaviewer.activity;


import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.viewmodel.HistoryViewModel;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public BaseFragment() {
        // Required empty public constructor
    }

    protected void startAsyncTask() {
        Observable.fromCallable(
                new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        getContentBackground();
                        return 1;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        updateContent();
                    }
                });
    }

    protected SettingViewModel getSettingViewModel() {
        return ((BaseActivity) this.getActivity()).getAppViewModel().Setting;
    }

    protected MangaViewModel getMangaViewModel() {
        return ((BaseActivity) this.getActivity()).getAppViewModel().Manga;
    }

    protected HistoryViewModel getHistoryViewModel() {
        return ((BaseActivity) this.getActivity()).getAppViewModel().HistoryManga;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);

        return textView;
    }


    public void refresh() {
    }

    protected void updateContent() {
    }

    protected Void getContentBackground() {
        return null;
    }

}
