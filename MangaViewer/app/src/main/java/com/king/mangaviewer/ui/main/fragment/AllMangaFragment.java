package com.king.mangaviewer.ui.main.fragment;


import com.king.mangaviewer.component.MangaGridView;
import com.king.mangaviewer.ui.main.fragment.HomeFragment;
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