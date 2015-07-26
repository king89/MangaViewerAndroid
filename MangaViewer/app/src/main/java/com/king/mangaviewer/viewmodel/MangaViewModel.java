package com.king.mangaviewer.viewmodel;

import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaPageItem;

import java.util.List;

public class MangaViewModel extends ViewModelBase {

    public MangaMenuItem selectedMangaMenuItem = null;
    public List<MangaChapterItem> mangaChapterList = null;
    public MangaChapterItem selectedMangaChapterItem = null;
    public List<MangaPageItem> mangaPageList = null;
    public MangaPageItem selectedMangaPageItem = null;
    List<MangaMenuItem> mangaMenuList = null;

    public List<MangaMenuItem> getMangaMenuList() {
        return mangaMenuList;
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
}
