package com.king.mangaviewer.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.king.mangaviewer.MangaPattern.WebSiteBasePattern;
import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.component.MangaGridView;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.util.MangaHelper;

import java.util.HashMap;
import java.util.List;


public class HomeFragment extends BaseFragment {

    public MangaGridView gv;
    TextView mangaSourceTv;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public HomeFragment() {
    }

    @Override
    public void refresh() {
        this.getMangaViewModel().setMangaMenuList(null);
        mangaSourceTv.setText(getSettingViewModel().getSelectedWebSource(getActivity()).getDisplayName());
        gv.refresh();
        mSwipeRefreshLayout.setRefreshing(false);
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

        mangaSourceTv = (TextView) rootView.findViewById(R.id.manga_source_textView);
        String selectedMangaSourceName = getSettingViewModel().getSelectedWebSource(getActivity()).getDisplayName();
        mangaSourceTv.setText(selectedMangaSourceName);
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