package com.king.mangaviewer.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;

import java.util.Collections;
import java.util.List;


public class HistoryFragment extends BaseFragment {

    private GridView gv;
    private TextView tv;

    public HistoryFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        //Toast.makeText(getActivity(),"OnResume",Toast.LENGTH_SHORT);
        getHistoryMangaList();
        gv.invalidate();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
        gv = (GridView) rootView.findViewById(R.id.gridView);
        tv = (TextView) rootView.findViewById(R.id.textView);
        getHistoryMangaList();
        return rootView;
    }

    private void getHistoryMangaList() {
        MainActivity copy = (MainActivity) getActivity();
        List<FavouriteMangaMenuItem> list = copy.getAppViewModel().Setting.getFavouriteMangaList();
        Collections.sort(list);
        Collections.reverse(list);
        MangaMenuItemAdapter adapter = new MangaMenuItemAdapter(copy, copy.getAppViewModel().Manga, list, true);
        gv.setAdapter(adapter);
        tv.setVisibility(View.GONE);
        if (list.size() == 0) {
            tv.setText(getString(R.string.history_no_history_manga));
            tv.setVisibility(View.VISIBLE);
        }

    }
}