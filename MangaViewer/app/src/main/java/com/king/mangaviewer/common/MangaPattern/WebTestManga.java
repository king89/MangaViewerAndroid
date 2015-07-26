package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.model.TitleAndUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KinG on 12/24/2014.
 */
public class WebTestManga extends WebSiteBasePattern {
    String LOG_TAG = "WebTestManga";

    public WebTestManga(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        WEBSITEURL = "";
        WEBSEARCHURL = "";
        CHARSET = "gb2312";
    }


    @Override
    public List<String> GetPageList(String firstPageUrl) {

        List<String> pageList = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            pageList.add("Page-" + i);
        }
        return pageList;
    }

    @Override
    public String GetImageUrl(String pageUrl, int nowNum) {
        return pageUrl;
    }


    @Override
    public List<TitleAndUrl> GetChapterList(String chapterUrl) {

        List<TitleAndUrl> chapterList = new ArrayList<TitleAndUrl>();

        for (int i = 0; i < 10; i++) {
            String url = "url-" + i;
            String title = "chapter-" + i;
            chapterList.add(new TitleAndUrl(title, url));
        }


        return chapterList;
    }

    @Override
    public List<TitleAndUrl> GetTopMangaList(String html) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();

        for (int i = 0; i < 10; i++) {
            String url = WEBSITEURL + i;
            String title = "Test Menu " + i;
            String imageUrl = "";
            topMangaList.add(new TitleAndUrl(title, url, imageUrl));

        }

        return topMangaList;
    }
}
