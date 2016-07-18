package com.king.mangaviewer.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.component.MangaGridView;
import com.king.mangaviewer.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.HashMap;
import java.util.List;


public class AllMangaFragment extends HomeFragment {
    @Override
    protected void setLoadMangaFunction() {
        gv.Initial(getMangaViewModel(), new MangaGridView.IGetMore() {
            @Override
            public void getMoreManga(List<MangaMenuItem> mMangaList, HashMap<String, Object> state) {
                new MangaHelper(getActivity()).getAllManga(mMangaList, state);
            }
        });
    }

}