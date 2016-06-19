package com.king.mangaviewer.activity;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.FavouriteMangaItemAdapter;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;

import java.util.Collections;
import java.util.List;


public class FavouriteFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tv;

    public FavouriteFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        //Toast.makeText(getActivity(),"OnResume",Toast.LENGTH_SHORT);
//        getFavouriteMangaList();
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

        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        tv = (TextView) rootView.findViewById(R.id.textView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFavouriteMangaList();
            }
        });

        getFavouriteMangaList();
        return rootView;
    }

    private void getFavouriteMangaList() {
        MainActivity copy = (MainActivity) getActivity();

        List<FavouriteMangaMenuItem> list = copy.getAppViewModel().Setting.getFavouriteMangaList();

        Collections.sort(list);
        Collections.reverse(list);

        gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.gridvivew_column_num));

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        FavouriteMangaItemAdapter rcAdapter = new FavouriteMangaItemAdapter(copy, copy.getAppViewModel().Manga, list);
        mRecyclerView.setAdapter(rcAdapter);

        tv.setVisibility(View.GONE);
        if (list.size() == 0) {
            tv.setText(getString(R.string.favourite_no_favourite_manga));
            tv.setVisibility(View.VISIBLE);
        }

        mSwipeRefreshLayout.setRefreshing(false);

    }
}