package com.king.mangaviewer.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.component.MangaGridView;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.HashMap;
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

        TextView mangaSourceTv = (TextView) rootView.findViewById(R.id.manga_source_textView);
        String selectedMangaSourceName = getSettingViewModel().getSelectedWebSource(getActivity()).getDisplayName();
        mangaSourceTv.setText(selectedMangaSourceName);

        TextView tv = (TextView) rootView.findViewById(R.id.textView);
        gv = (MangaGridView) rootView.findViewById(R.id.gridView);
        gv.setLoadingFooter(tv);
        //reset all manga list
        getMangaViewModel().resetAllMangaList();
        gv.Initial(getMangaViewModel(), new MangaGridView.IGetMore() {
            @Override
            public void getMoreManga(List<MangaMenuItem> mMangaList, HashMap<String, Object> state) {
                new MangaHelper(getActivity()).getAllManga(mMangaList, state);
            }
        });
        return rootView;
    }
}