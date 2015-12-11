package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.model.TitleAndUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

        int num = new Random().nextInt(100);
        for (int i = 0; i < num; i++) {
            String url = "url-" + i;
            String title = "chapterchapterchapterchapterchapterchapterchapterchapterchapterchapterchapter-" + i;
            chapterList.add(new TitleAndUrl(title, url));
        }


        return chapterList;
    }

    @Override
    public List<TitleAndUrl> getLatestMangaList(HashMap<String, Object> state) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();
        try {
            for (int i = 0; i < 10; i++) {
                String url = WEBSITEURL + i;
                String title = "Test MenuMenuMenuMenuMenuMenuMenuMenuMenuMenuMenuMenuMenu " + i;
                String imageUrl = "";
                topMangaList.add(new TitleAndUrl(title, url, imageUrl));

            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return topMangaList;
    }

    @Override
    public List<TitleAndUrl> getAllMangaList(HashMap<String, Object> state) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();
        try {
            for (int i = 0; i < 10; i++) {
                String url = WEBSITEURL + i;
                String title = "Test Menu " + i;
                String imageUrl = "";
                topMangaList.add(new TitleAndUrl(title, url, imageUrl));

            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return topMangaList;
    }

    @Override
    public String DownloadImgPage(String imgUrl, MangaPageItem pageItem, Constants.SaveType saveType, String refer) {
        return "/data/data/com.king.mangaviewer/files/Manga/WebHHComic/2daec2e9537278215341f33310f213de/053c8358510daf469ce0b68793e59b7a/z_0001_10370.JPG";
    }
}
