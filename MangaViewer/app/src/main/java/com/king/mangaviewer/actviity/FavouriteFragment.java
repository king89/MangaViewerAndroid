package com.king.mangaviewer.actviity;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FavouriteFragment extends BaseFragment {

    private GridView gv;
    private TextView tv;

    public FavouriteFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        //Toast.makeText(getActivity(),"OnResume",Toast.LENGTH_SHORT);
        getFavouriteMangaList();
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

        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);
        gv = (GridView) rootView.findViewById(R.id.gridView);
        tv = (TextView) rootView.findViewById(R.id.textView);
//        gv.setClickable(true);
//        gv.setLongClickable(true);
//        gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                new AlertDialog.Builder(getActivity())
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setTitle(getString(R.string.deleting_dialog_title))
//                        .setMessage(getString(R.string.do_you_want_to_delete))
//                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //finish();
//                            }
//
//                        })
//                        .setNegativeButton("No", null)
//                        .show();
//                return false;
//            }
//        });
        getFavouriteMangaList();
        return rootView;
    }

    private void getFavouriteMangaList() {
        MainActivity copy = (MainActivity) getActivity();

        List<FavouriteMangaMenuItem> list = copy.getAppViewModel().Setting.getFavouriteMangaList();

        Collections.sort(list);
        Collections.reverse(list);
        MangaMenuItemAdapter adapter = new MangaMenuItemAdapter(copy, copy.getAppViewModel().Manga, list, true);
        gv.setAdapter(adapter);
        tv.setVisibility(View.GONE);
        if (list.size() == 0) {
            tv.setText(getString(R.string.favourite_no_favourite_manga));
            tv.setVisibility(View.VISIBLE);
        }

    }
}