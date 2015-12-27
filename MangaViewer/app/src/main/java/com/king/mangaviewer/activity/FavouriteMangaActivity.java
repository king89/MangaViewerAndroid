package com.king.mangaviewer.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.widget.GridView;

import com.king.mangaviewer.R;

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