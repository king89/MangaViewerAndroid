package com.king.mangaviewer.viewmodel;

import android.content.Context;

import com.king.mangaviewer.model.MangaChapterItem;

import java.util.List;

/**
 * Created by KinG on 12/27/2015.
 */
public class HistoryViewModel extends ViewModelBase {
    private List<MangaChapterItem> mHistoryChapterList;

    public HistoryViewModel(Context context){
        super(context);
    }
    public List<MangaChapterItem> getHistoryChapterList() {
        return mHistoryChapterList;
    }
    public void setHistoryChapterList(List<MangaChapterItem> mHistoryChapterList) {
        this.mHistoryChapterList = mHistoryChapterList;
    }

    //add histroy
    public boolean addChapterItemToHistory(MangaChapterItem chapter){
        return true;
    }
}
