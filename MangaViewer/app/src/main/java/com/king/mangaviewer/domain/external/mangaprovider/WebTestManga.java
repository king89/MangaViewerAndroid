package com.king.mangaviewer.domain.external.mangaprovider;

import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.model.TitleAndUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.inject.Inject;

/**
 * Created by KinG on 12/24/2014.
 */
public class WebTestManga extends MangaProvider {
    String LOG_TAG = "WebTestManga";

    @Inject
    public WebTestManga() {
        // TODO Auto-generated constructor stub
        setWEBSITE_URL("");
        setWEB_SEARCH_URL("");
        setCHARSET("gb2312");
    }


    @Override
    public List<String> getPageList(String firstPageUrl) {

        List<String> pageList = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            pageList.add("Page-" + i);
        }
        return pageList;
    }

    @Override
    public String getImageUrl(String pageUrl, int nowNum) {
        return pageUrl;
    }


    @Override
    public List<TitleAndUrl> getChapterList(String chapterUrl) {

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
                String url = getWEBSITE_URL() + i;
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
                String url = getWEBSITE_URL() + i;
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
