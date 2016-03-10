package com.king.mangaviewer.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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
    ProgressDialog progressDialog;
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            MainActivity copy = (MainActivity) getActivity();
            MangaMenuItemAdapter adapter = new MangaMenuItemAdapter(copy,
                    copy.getAppViewModel().Manga,
                    copy.getAppViewModel().Manga.getMangaMenuList());
            gv.setAdapter(adapter);
        }

        ;

    };

    public HomeFragment() {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                this.getMangaViewModel().setMangaMenuList(null);
                gv.refresh();
                return true;
            default:
                return getActivity().onOptionsItemSelected(item);
        }
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
                new MangaHelper(getActivity()).getLatestMangeList(mMangaList, state);
            }
        });
        return rootView;
    }

}