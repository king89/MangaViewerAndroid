package com.king.mangaviewer.actviity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.common.component.MangaGridView;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.List;


public class AllMangaFragment extends BaseFragment {

    public MangaGridView gv;
    ProgressDialog progressDialog;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        }

    };

    public AllMangaFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_manga, container, false);

        gv = (MangaGridView) rootView.findViewById(R.id.gridView);

        //reset all manga list
        getMangaViewModel().resetAllMangaList();
        gv.Initial(getMangaViewModel());
        return rootView;
    }
}