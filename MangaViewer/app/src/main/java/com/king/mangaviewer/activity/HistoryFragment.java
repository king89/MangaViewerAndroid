package com.king.mangaviewer.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.HistoryChapterItemAdapter;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.HistoryMangaChapterItem;

import java.util.Collections;
import java.util.List;


public class HistoryFragment extends BaseFragment {

    private ListView lv;
    private TextView tv;

    public HistoryFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        //Toast.makeText(getActivity(),"OnResume",Toast.LENGTH_SHORT);
        getHistoryMangaList();
        lv.invalidate();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.history_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        lv = (ListView) rootView.findViewById(R.id.listView);
        tv = (TextView) rootView.findViewById(R.id.textView);
        getHistoryMangaList();
        return rootView;
    }

    private void getHistoryMangaList() {
        MainActivity copy = (MainActivity) getActivity();
        List<HistoryMangaChapterItem> list = copy.getAppViewModel().HistoryManga.getHistoryChapterList();

        BaseAdapter adapter = new HistoryChapterItemAdapter(copy, copy.getAppViewModel().Manga, list);
        lv.setAdapter(adapter);
        tv.setVisibility(View.GONE);
        if (list.size() == 0) {
            tv.setText(getString(R.string.history_no_history_manga));
            tv.setVisibility(View.VISIBLE);
        }

    }
}