package com.king.mangaviewer.actviity;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.List;


public class HomeFragment extends Fragment {

    public GridView gv;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        gv = (GridView) rootView.findViewById(R.id.gridView);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gv.setNumColumns(5);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gv.setNumColumns(3);
        }
        progressDialog = ProgressDialog.show(this.getActivity(), "Loading",
                "Loading");

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                MainActivity copy = (MainActivity) getActivity();
                List<MangaMenuItem> mList = copy.getMangaHelper()
                        .GetNewMangeList();
                copy.getAppViewModel().Manga
                        .setMangaMenuList(mList);

                handler.sendEmptyMessage(0);
            }
        }.start();

        return rootView;
    }
}