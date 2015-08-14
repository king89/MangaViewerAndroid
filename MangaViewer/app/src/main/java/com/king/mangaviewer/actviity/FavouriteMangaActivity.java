package com.king.mangaviewer.actviity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.GridView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.List;

/**
 * Created by KinG on 7/23/2015.
 */
public class FavouriteMangaActivity extends BaseActivity {

    private GridView gv;
    private ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        gv = (GridView) this.findViewById(R.id.gridView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView

        return true;
    }

    @Override
    protected boolean IsCanBack() {
        return true;
    }
}