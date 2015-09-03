package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by KinG on 9/2/2015.
 */
public class WebMangaReader extends WebSiteBasePattern {

    private final static int PAGE_SIZE = 30;

    public WebMangaReader(Context context) {
        super(context);
        WEBSITEURL = "http://www.mangareader.net/";
        WEBLATESTMANGABASEURL = "http://www.mangareader.net/";
        WEBSEARCHURL = "http://www.mangareader.net/search/?w=%s&rd=0&status=0&order=0&p=%d";
        WEBALLMANGABASEURL = "http://www.mangareader.net/popular/%d";
        CHARSET = "utf8";
    }

    //Menu
    @Override
    protected List<TitleAndUrl> getLatestMangaList(String html) {
        List<TitleAndUrl> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements els = doc.select(".chapter");

        Iterator i = els.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = checkUrl(e.attr("href"));
            String title = e.text();
            String imageUrl = "";
            list.add(new TitleAndUrl(title, url, imageUrl));
        }
        return list;
    }

    @Override
    public String getMenuCover(MangaMenuItem menu) {
        String html = getHtml(menu.getUrl());
        Document doc = Jsoup.parse(html);
        return doc.select("#mangaimg img").attr("src");
    }


    //Chapter

    @Override
    public List<TitleAndUrl> GetChapterList(String chapterUrl) {
        String html = getHtml(chapterUrl);
        List<TitleAndUrl> list = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements els = doc.select("#chapterlist a");
        Iterator i = els.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = checkUrl(e.attr("href"));
            String title = e.text();
            list.add(new TitleAndUrl(title, url, null));
        }
        Collections.reverse(list);
        return list;

    }

    //Page

    @Override
    public List<String> GetPageList(String firstPageUrl) {
        List<String> pageList = new ArrayList<>();
        String html = getHtml(firstPageUrl);
        int total = GetTotalNum(html);

        for (int i = 1; i <= total; i++) {
            pageList.add(firstPageUrl + "/" + i);
        }
        return pageList;
    }

    @Override
    public int GetTotalNum(String html) {
        Document doc = Jsoup.parse(html);
        return doc.select("#pageMenu option").size();
    }

    @Override
    public String GetImageUrl(String pageUrl, int nowPage) {
        String html = getHtml(pageUrl);
        Document doc = Jsoup.parse(html);
        return doc.select("#img").attr("src");
    }
}
