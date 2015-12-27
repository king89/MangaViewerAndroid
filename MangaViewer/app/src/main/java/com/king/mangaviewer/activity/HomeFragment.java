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

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.List;


public class HomeFragment extends BaseFragment {

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                this.getMangaViewModel().setMangaMenuList(null);
                loadMenuList();
                return true;
            default:
                return getActivity().onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView tv = (TextView) rootView.findViewById(R.id.manga_source_textView);
        String selectedMangaSourceName = getSettingViewModel().getSelectedWebSource(getActivity()).getDisplayName();
        tv.setText(selectedMangaSourceName);
        gv = (GridView) rootView.findViewById(R.id.gridView);
        loadMenuList();

        return rootView;
    }

    private void loadMenuList() {
        progressDialog = ProgressDialog.show(this.getActivity(), getString(R.string.title_loading),
                getString(R.string.msg_loading));

        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                MainActivity copy = (MainActivity) getActivity();

                List<MangaMenuItem> mList = copy.getAppViewModel().Manga.getMangaMenuList();
                if (mList == null) {
                    mList = copy.getMangaHelper().getLatestMangeList();
                    copy.getAppViewModel().Manga
                            .setMangaMenuList(mList);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }
}