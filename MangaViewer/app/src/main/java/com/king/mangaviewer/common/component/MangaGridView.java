package com.king.mangaviewer.common.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 8/24/2015.
 */
public class MangaGridView extends GridView {

    private String LOG = "MangaGridView";
    private HashMap<String, Object> mStateHash;
    private boolean flagLoading;
    private Object flagLock = new Object();
    private List<MangaMenuItem> mMangaList;
    private View mLoadingFooter;
    private MangaViewModel mMangaViewModel;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (MangaGridView.this.getAdapter() != null) {
                ((BaseAdapter) MangaGridView.this.getAdapter()).notifyDataSetChanged();
            }
            if (mLoadingFooter != null) {
                mLoadingFooter.setVisibility(GONE);
            }
            setFlagLoading(false);
            super.handleMessage(msg);
        }
    };

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MangaGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Init();
    }

    public void Initial(MangaViewModel mangaViewModel) {
        mMangaViewModel = mangaViewModel;
        this.mMangaList = mangaViewModel.getAllMangaList();
        this.mStateHash = mangaViewModel.getmAllMangaStateHash();
        MangaGridView.this.setAdapter(new MangaMenuItemAdapter(getContext(), mMangaViewModel, mMangaList));
        getMoreManga();
    }

    private void Init() {
        this.setOnScrollListener(mOnScrollListener);
        mLoadingFooter = new TextView(getContext());
    }

    public void setLoadingFooter(View view) {
        mLoadingFooter = view;
    }

    protected void getMoreManga() {
        setFlagLoading(true);
        mLoadingFooter.setVisibility(VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new MangaHelper(getContext()).getAllManga(mMangaList, mStateHash);

                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i(LOG, "" + scrollState);
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                if (getFlagLoading() == false) {
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
}
