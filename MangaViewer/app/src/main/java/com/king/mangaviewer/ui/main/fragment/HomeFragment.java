package com.king.mangaviewer.ui.main.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.base.BaseFragment;
import com.king.mangaviewer.component.MangaGridView;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.util.MangaHelper;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends BaseFragment {

    public MangaGridView gv;
    TextView tvMangaSource;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public HomeFragment() {
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void refresh() {
        this.getMangaViewModel().setMangaMenuList(null);
        tvMangaSource.setText(getSettingViewModel().getSelectedWebSource().getDisplayName());
        gv.refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return getActivity().onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = setContentView(inflater, container);

        tvMangaSource = (TextView) rootView.findViewById(R.id.manga_source_textView);
        String selectedMangaSourceName = getSettingViewModel().getSelectedWebSource().getDisplayName();
        tvMangaSource.setText(selectedMangaSourceName);
        TextView tv = (TextView) rootView.findViewById(R.id.textView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        gv = (MangaGridView) rootView.findViewById(R.id.gridView);
        gv.setLoadingFooter(tv);
        gv.setSwipeRefreshLayout(mSwipeRefreshLayout);
        gv.setAdapter(null);
        //reset all manga list
        getMangaViewModel().resetAllMangaList();
        setLoadMangaFunction();
        return rootView;
    }

    protected View setContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_manga_gridview, container, false);
    }

    protected void setLoadMangaFunction() {
        gv.Initial(getMangaViewModel(), new MangaGridView.IGetMore() {
            @Override
            public void getMoreManga(List<MangaMenuItem> mMangaList, HashMap<String, Object> state) {
                new MangaHelper(getActivity()).getLatestMangeList(mMangaList, state);
            }
        });
    }

}