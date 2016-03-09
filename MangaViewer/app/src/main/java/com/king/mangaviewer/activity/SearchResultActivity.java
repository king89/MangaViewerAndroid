package com.king.mangaviewer.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.MangaPattern.WebSiteBasePattern;
import com.king.mangaviewer.component.MangaGridView;
import com.king.mangaviewer.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 7/23/2015.
 */
public class SearchResultActivity extends BaseActivity {

    public MangaGridView gv;
    private ProgressDialog progressDialog;
    HashMap<String, Object> state = new HashMap<String, Object>();
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
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
        if (searchView != null){
            searchView.onActionViewCollapsed();
        }
    }

    private void getQueryResult(final String query) {
        gv = (MangaGridView) this.findViewById(R.id.gridView);

        TextView mangaSourceTv = (TextView) this.findViewById(R.id.manga_source_textView);
        String selectedMangaSourceName = getAppViewModel().Setting.getSelectedWebSource(this).getDisplayName();
        mangaSourceTv.setText(selectedMangaSourceName);

        TextView tv = (TextView) findViewById(R.id.textView);
        gv = (MangaGridView) findViewById(R.id.gridView);
        gv.setLoadingFooter(tv);
        //reset all manga list
        this.getAppViewModel().Manga.resetAllMangaList();
        this.getAppViewModel().Manga.getAllMangaStateHash().put(WebSiteBasePattern.STATE_SEARCH_QUERYTEXT, query);

        gv.Initial(this.getAppViewModel().Manga, new MangaGridView.IGetMore() {
            @Override
            public void getMoreManga(List<MangaMenuItem> mMangaList, HashMap<String, Object> state) {
                new MangaHelper(SearchResultActivity.this).getSearchMangeList(mMangaList, state);
            }
        });
    }

    @Override
    protected boolean IsCanBack() {
        return true;
    }
}