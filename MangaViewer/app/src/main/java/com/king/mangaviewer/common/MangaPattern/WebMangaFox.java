package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by KinG on 8/31/2015.
 */
public class WebMangaFox extends WebSiteBasePattern {


    private static String mRestSearchString = "&type=&author_method=cw&author=&artist_method=cw&artist=&genres[Action]=0&genres[Adult]=0&genres[Adventure]=0&genres[Comedy]=0&genres[Doujinshi]=0&genres[Drama]=0&genres[Ecchi]=0&genres[Fantasy]=0&genres[Gender%20Bender]=0&genres[Harem]=0&genres[Historical]=0&genres[Horror]=0&genres[Josei]=0&genres[Martial%20Arts]=0&genres[Mature]=0&genres[Mecha]=0&genres[Mystery]=0&genres[One%20Shot]=0&genres[Psychological]=0&genres[Romance]=0&genres[School%20Life]=0&genres[Sci-fi]=0&genres[Seinen]=0&genres[Shoujo]=0&genres[Shoujo%20Ai]=0&genres[Shounen]=0&genres[Shounen%20Ai]=0&genres[Slice%20of%20Life]=0&genres[Smut]=0&genres[Sports]=0&genres[Supernatural]=0&genres[Tragedy]=0&genres[Webtoons]=0&genres[Yaoi]=0&genres[Yuri]=0&released_method=eq&released=&rating_method=eq&rating=&is_completed=&advopts=1";

    public WebMangaFox(Context context) {
        super(context);
        WEBSITEURL = "http://mangafox.me/";
        WEBLATESTMANGABASEURL = "http://mangafox.me/directory/%s?latest";
        WEBSEARCHURL = "http://mangafox.me/search.php?name_method=cw&name=%s&page=%d";
        WEBALLMANGABASEURL = "http://mangafox.me/directory/";
        CHARSET = "utf8";
    }

    //Menu
    @Override
    public List<TitleAndUrl> getLatestMangaList(String html) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();

        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".title");
        Iterator i = el.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = e.select("a").attr("href");
            String title = e.select("a").text();
            String imageUrl = "";
            topMangaList.add(new TitleAndUrl(title, url, imageUrl));
        }

        return topMangaList;

    }

    @Override
    public String getMenuCover(MangaMenuItem menu) {
        String html = getHtml(menu.getUrl());
        Document doc = Jsoup.parse(html);
        String url = doc.select(".cover img").attr("src");
        return url;
    }

    //Chapter

    @Override
    public List<TitleAndUrl> GetChapterList(String chapterUrl) {
        List<TitleAndUrl> chapterList = new ArrayList<>();

        String html = getHtml(chapterUrl);
        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".chlist");

        for (int i = 0; i < el.size(); i++) {
            Elements els = el.get(i).select(".tips");
            String vol = el.get(i).previousElementSibling().select(".volume").first().textNodes().get(0).text();

            Iterator it = els.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String url = e.attr("href");
                String title = e.text();
                if (!vol.toLowerCase().contains("not")) {
                    title = title + " - " + vol;
                }
                if (url.startsWith("/")) {
                    url = WEBSITEURL + url;
                }

                chapterList.add(new TitleAndUrl(title, url));
            }
        }

        return chapterList;
    }


    //Page


    @Override
    public List<String> GetPageList(String firstPageUrl) {
        List<String> pageList = new ArrayList<>();
        String html = getHtml(firstPageUrl);
        int total = GetTotalNum(html);

        String fileName = firstPageUrl.substring(firstPageUrl.lastIndexOf("/"));
        String preFileName = firstPageUrl.substring(0, firstPageUrl.lastIndexOf("/"));

        if (fileName.isEmpty()) {
            for (int i = 1; i <= total; i++) {
                pageList.add(preFileName + i + ".html");
            }
        }else {
            for (int i = 1; i <= total; i++) {
                pageList.add(preFileName + fileName.replace("1", i + ""));
            }
        }
        return pageList;
    }

    @Override
    public String GetImageUrl(String pageUrl, int nowPage) {
        String html = getHtml(pageUrl);
        Document doc = Jsoup.parse(html);
        return doc.select("#image").attr("src");
    }

    @Override
    public List<TitleAndUrl> getSearchingList(HashMap<String, Object> state) {
        return super.getSearchingList(state);
    }
}
