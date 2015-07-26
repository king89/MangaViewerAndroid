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
public class SearchResultActivity extends BaseActivity {

    private GridView gv;
    private ProgressDialog progressDialog;
    public android.os.Handler handler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {

            MangaMenuItemAdapter adapter = new MangaMenuItemAdapter(SearchResultActivity.this,
                    SearchResultActivity.this.getAppViewModel().Manga,
                    SearchResultActivity.this.getAppViewModel().Manga.getMangaMenuList());
            gv.setAdapter(adapter);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

        }

        ;

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        gv = (GridView) this.findViewById(R.id.gridView);
        progressDialog = ProgressDialog.show(this, "Loading",
                "Loading");

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            this.getSupportActionBar().setTitle(query);
            getQueryResult(query);
        }
    }

    private void getQueryResult(final String query) {
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub

                List<MangaMenuItem> mList = SearchResultActivity.this.getMangaHelper()
                        .GetSearchMangeList(query, 0);
                SearchResultActivity.this.getAppViewModel().Manga
                        .setMangaMenuList(mList);

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    protected boolean IsCanBack() {
        return true;
    }
}