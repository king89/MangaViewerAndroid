package com.king.mangaviewer.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.MangaPattern.WebSiteBasePattern;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 8/24/2015.
 */
public class MangaGridView extends RecyclerView {

    private String LOG = "MangaGridView";
    private HashMap<String, Object> mStateHash;
    private boolean flagLoading;
    private Object flagLock = new Object();
    private List<MangaMenuItem> mMangaList;
    private View mLoadingFooter;
    private MangaViewModel mMangaViewModel;
    private IGetMore mIGetMoreManga;
    private boolean mNoMore = false;
    private GridLayoutManager mGridLayoutManager;

    public MangaGridView(Context context) {
        super(context);
        Init();
    }

    public MangaGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init();
    }

    public MangaGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
    }

    public void Initial(MangaViewModel mangaViewModel, IGetMore func) {
        mMangaViewModel = mangaViewModel;
        this.mMangaList = mangaViewModel.getAllMangaList();
        this.mStateHash = mangaViewModel.getAllMangaStateHash();
        this.setIGetMoreMangaFunciton(func);
        mGridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.gridvivew_column_num));
        MangaGridView.this.setLayoutManager(mGridLayoutManager);
        MangaGridView.this.setAdapter(new MangaMenuItemAdapter(getContext(), mMangaViewModel, mMangaList));
        getMoreManga();
    }

    private void Init() {
        this.setOnScrollListener(mOnScrollListener);
        mLoadingFooter = new TextView(getContext());
    }

    public void refresh() {
        mMangaList.clear();
        mStateHash.clear();
        this.scrollToPosition(0);
        getMoreManga();
    }

    public void setLoadingFooter(View view) {
        mLoadingFooter = view;
    }

    protected void getMoreManga() {
        setFlagLoading(true);
        mLoadingFooter.setVisibility(VISIBLE);
        new GetMoreMangaTask().execute();

    }

    OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int VISIBLE_THRESHOLD = 3;
            int firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            if (firstVisibleItem + visibleItemCount >= (totalItemCount - VISIBLE_THRESHOLD) && totalItemCount != 0) {
                if (getFlagLoading() == false && !mNoMore) {
                    getMoreManga();
                }
            }
        }
    };

    public void setFlagLoading(boolean flagLoading) {
        synchronized (flagLock) {
            this.flagLoading = flagLoading;
        }
    }

    public boolean getFlagLoading() {
        synchronized (flagLock) {
            return flagLoading;
        }
    }

    public interface IGetMore {
        public void getMoreManga(List<MangaMenuItem> menuList, HashMap<String, Object> state);
    }

    public void setIGetMoreMangaFunciton(IGetMore func) {
        this.mIGetMoreManga = func;
    }

    class GetMoreMangaTask extends AsyncTask<Void, Void, List<MangaMenuItem>> {

        @Override
        protected List<MangaMenuItem> doInBackground(Void... params) {
            List<MangaMenuItem> list = new ArrayList<>();
            if (mIGetMoreManga != null) {
                mIGetMoreManga.getMoreManga(list, mStateHash);
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<MangaMenuItem> list) {

            if (MangaGridView.this.getAdapter() != null) {
                int before = mMangaList.size();
                mMangaList.addAll(list);
                int after = mMangaList.size();
                MangaGridView.this.getAdapter().notifyItemRangeInserted(before, after);
            }
            if (mLoadingFooter != null) {
                mLoadingFooter.setVisibility(GONE);
            }
            if (mStateHash.containsKey(WebSiteBasePattern.STATE_NO_MORE)) {
                mNoMore = (boolean) mStateHash.get(WebSiteBasePattern.STATE_NO_MORE);
            }
            setFlagLoading(false);
        }
    }
}
