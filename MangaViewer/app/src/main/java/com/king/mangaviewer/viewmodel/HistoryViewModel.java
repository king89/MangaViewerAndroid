package com.king.mangaviewer.viewmodel;

import android.content.Context;

import com.king.mangaviewer.datasource.HistoryMangaDataSource;
import com.king.mangaviewer.model.HistoryMangaChapterItem;
import com.king.mangaviewer.model.MangaChapterItem;

import java.util.List;

/**
 * Created by KinG on 12/27/2015.
 */
public class HistoryViewModel extends ViewModelBase {
    private List<HistoryMangaChapterItem> mHistoryChapterList;
    private HistoryMangaDataSource mHistoryMangaDataSource;
    public HistoryViewModel(Context context){
        super(context);
        this.mHistoryMangaDataSource = new HistoryMangaDataSource(context);
    }
    public List<HistoryMangaChapterItem> getHistoryChapterList() {
        return mHistoryMangaDataSource.getAllHistoryMangaItem();
    }

    //add histroy
    public boolean addChapterItemToHistory(MangaChapterItem chapter){
        HistoryMangaChapterItem historyItem = new HistoryMangaChapterItem(chapter);
        mHistoryMangaDataSource.addToHistory(historyItem);
        return true;
    }
}
