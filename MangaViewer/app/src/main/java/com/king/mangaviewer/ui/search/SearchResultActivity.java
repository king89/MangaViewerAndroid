package com.king.mangaviewer.ui.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.base.BaseActivity;
import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider;
import com.king.mangaviewer.component.MangaGridView;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 7/23/2015.
 */
public class SearchResultActivity extends BaseActivity {

    public MangaGridView gv;
    HashMap<String, Object> state = new HashMap<String, Object>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;
    String queryString = "";
    TextView mangaSourceTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    @Override
    protected void initControl() {
        setContentView(R.layout.activity_search_result);

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
            queryString = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            this.getSupportActionBar().setTitle(queryString);
            getQueryResult(queryString);
        }
        if (searchView != null) {
            searchView.onActionViewCollapsed();
        }
    }

    private void getQueryResult(final String query) {
        gv = (MangaGridView) this.findViewById(R.id.gridView);
        swipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setSearchResult(query);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        mangaSourceTv = (TextView) this.findViewById(R.id.manga_source_textView);
        TextView tv = (TextView) findViewById(R.id.textView);
        gv = (MangaGridView) findViewById(R.id.gridView);
        gv.setLoadingFooter(tv);
        setSearchResult(query);

    }

    private void setSearchResult(String query) {
        mangaSourceTv.setText(getAppViewModel().Setting.getSelectedWebSource().getDisplayName());

        //reset all manga list
        this.getAppViewModel().Manga.resetAllMangaList();
        this.getAppViewModel().Manga.getAllMangaStateHash().put(MangaProvider.STATE_SEARCH_QUERYTEXT, query);

        //gv.Initial(this.getAppViewModel().Manga, new MangaGridView.MangaGridViewCallback() {
        //    @Override
        //    public void getMoreManga(List<MangaMenuItem> mMangaList, HashMap<String, Object> state) {
        //        new MangaHelper(SearchResultActivity.this).getSearchMangeList(mMangaList, state);
        //    }
        //});
    }


    private void displayMangaSource(View anchorView) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.menu_main_setting, null);

        ListView lv = (ListView) layout.findViewById(R.id.listView);

        final List<MangaWebSource> mws = getAppViewModel().Setting.getMangaWebSources();
        int tSelectWebSourcePos = 0;
        List<String> source = new ArrayList<>();
        if (mws != null) {
            int i = 0;
            for (MangaWebSource m : mws) {
                source.add(m.getDisplayName());
                if (m.getId() == getAppViewModel().Setting.getSelectedWebSource().getId()) {
                    tSelectWebSourcePos = i;
                }
                i++;
            }
        }

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, source));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAppViewModel().Setting.setSelectedWebSource(mws.get(position), SearchResultActivity.this);
                getAppViewModel().Manga.setMangaMenuList(null);
                setSearchResult(queryString);
                popup.dismiss();
            }
        });
        lv.setItemChecked(tSelectWebSourcePos, true);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.showAsDropDown(anchorView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_setting:
                View v = findViewById(R.id.menu_setting);
                displayMangaSource(v);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}