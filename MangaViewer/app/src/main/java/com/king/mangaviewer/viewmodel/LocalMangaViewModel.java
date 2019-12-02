package com.king.mangaviewer.viewmodel;

import com.king.mangaviewer.domain.external.mangaprovider.LocalMangaProvider;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.model.TitleAndUrl;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class LocalMangaViewModel extends MangaViewModel {

    private String selectedFilePath;

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(File path, String fileName) {
        //selectedFilePath = path;
        LocalMangaProvider tLocalManga = new LocalMangaProvider();
        MangaMenuItem tmenu = new MangaMenuItem("","","","",path.getAbsolutePath(), new MangaWebSource(-1,"","",
            LocalMangaProvider.class.getName(), -1, null, -1, true));

        List<TitleAndUrl> chapterList = tLocalManga.getChapterList(tmenu);

        MangaChapterItem tSelectedChapter = null;


        List<MangaChapterItem> list = new ArrayList<MangaChapterItem>();
        for (int i = 0; i < chapterList.size(); i++) {
            MangaChapterItem item = new MangaChapterItem("Chapter-" + i, chapterList.get(i)
                    .getTitle(), "", chapterList.get(i).getImagePath(),
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
