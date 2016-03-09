package com.king.mangaviewer.viewmodel;

import com.king.mangaviewer.MangaPattern.LocalManga;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.model.TitleAndUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalMangaViewModel extends MangaViewModel {

    private String selectedFilePath;

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(File path, String fileName) {
        //selectedFilePath = path;
        LocalManga tLocalManga = new LocalManga(null);
        List<TitleAndUrl> chapterList = tLocalManga.GetChapterList(path.getAbsolutePath());

        MangaChapterItem tSelectedChapter = null;

        MangaMenuItem tmenu = new MangaMenuItem(null,null,null,null,null, new MangaWebSource(-1,null,null,LocalManga.class.getName(),-1,null,-1));
        List<MangaChapterItem> list = new ArrayList<MangaChapterItem>();
        for (int i = 0; i < chapterList.size(); i++) {
            MangaChapterItem item = new MangaChapterItem("Chapter-" + i, chapterList.get(i)
                    .getTitle(), null, chapterList.get(i).getImagePath(),
                    chapterList.get(i).getUrl(), tmenu);
            list.add(item);
            if (item.getTitle().equalsIgnoreCase(fileName))
            {
                tSelectedChapter = item;
            }
        }
        this.setMangaChapterList(list);

        this.setSelectedMangaChapterItem(tSelectedChapter);

    }
}
