package com.king.mangaviewer.viewmodel;

import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaPageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MangaViewModel extends ViewModelBase {

    private MangaMenuItem selectedMangaMenuItem = null;
    private List<MangaChapterItem> mangaChapterList = null;
    private MangaChapterItem selectedMangaChapterItem = null;
    private List<MangaPageItem> mangaPageList = null;
    private MangaPageItem selectedMangaPageItem = null;
    private List<MangaMenuItem> mangaMenuList = null;

    private  List<MangaMenuItem> mAllMangaList = null;
    private HashMap<String, Object> mAllMangaStateHash = null;
    private int nowPagePosition = 0;

    public List<MangaMenuItem> getMangaMenuList() {
        return mangaMenuList;
    }

    public List<MangaMenuItem> getAllMangaList(){
        if (mAllMangaList == null)
            mAllMangaList = new ArrayList<>();
        return mAllMangaList;
    }

    public void setAllMangaStateHash(HashMap<String, Object> mAllMangaStateHash) {
        this.mAllMangaStateHash = mAllMangaStateHash;
    }

    public HashMap<String, Object> getmAllMangaStateHash() {
        if (mAllMangaStateHash == null)
            mAllMangaStateHash = new HashMap<>();
        return mAllMangaStateHash;
    }

    public void resetAllMangaList() {
        mAllMangaList = null;
        mAllMangaStateHash = null;
    }

    public void setAllMangaList(List<MangaMenuItem> mAllMangaList) {
        this.mAllMangaList = mAllMangaList;
    }

    public void setMangaMenuList(List<MangaMenuItem> mangaMenuList) {
        this.mangaMenuList = mangaMenuList;
    }

    public MangaMenuItem getSelectedMangaMenuItem() {
        return selectedMangaMenuItem;
    }

    public void setSelectedMangaMenuItem(MangaMenuItem selectedMangaMenuItem) {
        this.selectedMangaMenuItem = selectedMangaMenuItem;
    }

    public List<MangaChapterItem> getMangaChapterList() {
        return mangaChapterList;
    }

    public void setMangaChapterList(List<MangaChapterItem> mangaChapterList) {
        this.mangaChapterList = mangaChapterList;
    }

    public MangaChapterItem getSelectedMangaChapterItem() {
        return selectedMangaChapterItem;
    }

    public void setSelectedMangaChapterItem(
            MangaChapterItem selectedMangaChapterItem) {
        this.selectedMangaChapterItem = selectedMangaChapterItem;
        this.nowPagePosition = 0;
    }
    public void setSelectedMangaChapterItem(int index){
        if (getMangaChapterList() != null) {
            setSelectedMangaChapterItem(getMangaChapterList().get(index));
        }else{
            this.selectedMangaChapterItem = null;
        }
    }
    public List<MangaPageItem> getMangaPageList() {
        return mangaPageList;
    }

    public void setMangaPageList(List<MangaPageItem> mangaPageList) {
        this.mangaPageList = mangaPageList;
    }

    public MangaPageItem getSelectedMangaPageItem() {
        return selectedMangaPageItem;
    }

    public void setSelectedMangaPageItem(MangaPageItem selectedMangaPageItem) {
        this.selectedMangaPageItem = selectedMangaPageItem;
    }

    public int getNowPagePosition()
    {
        return nowPagePosition;
    }
    public void setNowPagePosition(int p)
    {
        nowPagePosition = p;
    }

}
