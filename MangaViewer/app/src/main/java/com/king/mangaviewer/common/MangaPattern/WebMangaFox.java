package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;
import android.util.Log;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by KinG on 8/31/2015.
 */
public class WebMangaFox extends WebSiteBasePattern {


    private static String mRestSearchString = "&type=&author_method=cw&author=&artist_method=cw&artist=&genres%5BAction%5D=0&genres%5BAdult%5D=0&genres%5BAdventure%5D=0&genres%5BComedy%5D=0&genres%5BDoujinshi%5D=0&genres%5BDrama%5D=0&genres%5BEcchi%5D=0&genres%5BFantasy%5D=0&genres%5BGender+Bender%5D=0&genres%5BHarem%5D=0&genres%5BHistorical%5D=0&genres%5BHorror%5D=0&genres%5BJosei%5D=0&genres%5BMartial+Arts%5D=0&genres%5BMature%5D=0&genres%5BMecha%5D=0&genres%5BMystery%5D=0&genres%5BOne+Shot%5D=0&genres%5BPsychological%5D=0&genres%5BRomance%5D=0&genres%5BSchool+Life%5D=0&genres%5BSci-fi%5D=0&genres%5BSeinen%5D=0&genres%5BShoujo%5D=0&genres%5BShoujo+Ai%5D=0&genres%5BShounen%5D=0&genres%5BShounen+Ai%5D=0&genres%5BSlice+of+Life%5D=0&genres%5BSmut%5D=0&genres%5BSports%5D=0&genres%5BSupernatural%5D=0&genres%5BTragedy%5D=0&genres%5BWebtoons%5D=0&genres%5BYaoi%5D=0&genres%5BYuri%5D=0&released_method=eq&released=&rating_method=eq&rating=&is_completed=&advopts=1&sort=views&order=za";
    private static String LOG_TAG = "WebMangaFox";

    public WebMangaFox(Context context) {
        super(context);
        WEBSITEURL = "http://mangafox.me/";
        WEBLATESTMANGABASEURL = "http://mangafox.me/directory/%s?latest";
        WEBSEARCHURL = "http://mangafox.me/search.php?name_method=cw&name=%s&page=%d%s";
        WEBALLMANGABASEURL = "http://mangafox.me/directory/%d.htm";
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

    @Override
    public List<TitleAndUrl> getAllManga(HashMap<String, Object> state) {

        boolean noMore = false;
        if (state.containsKey(STATE_NO_MORE)) {
            noMore = (boolean) state.get(STATE_NO_MORE);
        }

        if (!noMore) {
            int pageNum = 1;
            int totalNum = 0;
            String html = "";
            //no total num means first time
            if (!state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY)) {

                String turl = String.format(WEBALLMANGABASEURL, pageNum);
                Log.v(LOG_TAG, "All Manga: " + turl);
                html = getHtml(turl);
                totalNum = getSearchTotalNum(html);
                state.put(STATE_TOTAL_PAGE_NUM_THIS_KEY, totalNum);
            } else {
                if (state.containsKey(STATE_PAGE_NUM_NOW)) {
                    pageNum = (int) state.get(STATE_PAGE_NUM_NOW);
                }

                totalNum = (int) state.get(STATE_TOTAL_PAGE_NUM_THIS_KEY);
                if (pageNum + 1 <= totalNum) {
                    pageNum++;
                    state.put(STATE_PAGE_NUM_NOW, pageNum);
                } else {
                    state.put(STATE_NO_MORE, true);
                    return null;
                }

                String turl = String.format(WEBALLMANGABASEURL, pageNum);
                Log.v(LOG_TAG, "All Manga: " + turl);
                html = getHtml(turl);
            }

            List<TitleAndUrl> mangaList = new ArrayList<>();

            Document doc = Jsoup.parse(html);
            Elements el = doc.select(".list li");
            for (int i = 0; i < el.size(); i++) {
                String title = el.get(i).select(".title").text();
                String url = el.get(i).select(".title").attr("href");
                if (url.startsWith("/")) {
                    url = WEBSITEURL + url;
                }
                String imageUrl = el.get(i).select(".manga_img img").attr("src");
                mangaList.add(new TitleAndUrl(title, url, imageUrl));
            }
            return mangaList;
        } else {
            return null;
        }
    }

    @Override
    public List<TitleAndUrl> getSearchingList(HashMap<String, Object> state) {

        boolean noMore = false;
        if (state.containsKey(STATE_NO_MORE)) {
            noMore = (boolean) state.get(STATE_NO_MORE);
        }

        if (!noMore) {
            String queryText = "";
            try {
                queryText = state.get(STATE_SEARCH_QUERYTEXT).toString();
                queryText = java.net.URLEncoder.encode(queryText, CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int pageNum = 1;
            int totalNum = 0;
            String html = "";
            //no total num means first time
            if (!state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY)) {

                String turl = String.format(WEBSEARCHURL, queryText, pageNum, mRestSearchString);
                Log.v(LOG_TAG, "Search: " + turl);
                html = getHtml(turl);
                totalNum = getSearchTotalNum(html);
                state.put(STATE_TOTAL_PAGE_NUM_THIS_KEY, totalNum);
            } else {
                if (state.containsKey(STATE_PAGE_NUM_NOW)) {
                    pageNum = (int) state.get(STATE_PAGE_NUM_NOW);
                }

                totalNum = (int) state.get(STATE_TOTAL_PAGE_NUM_THIS_KEY);
                if (pageNum + 1 <= totalNum) {
                    pageNum++;
                    state.put(STATE_PAGE_NUM_NOW, pageNum);
                } else {
                    state.put(STATE_NO_MORE, true);
                    return null;
                }

                String turl = String.format(WEBSEARCHURL, queryText, pageNum, mRestSearchString);
                Log.v(LOG_TAG, "Search: " + turl);
                html = getHtml(turl);
            }

            List<TitleAndUrl> mangaList = new ArrayList<>();

            Document doc = Jsoup.parse(html);
            Elements el = doc.select(".series_preview");
            for (int i = 0; i < el.size(); i++) {
                String title = el.get(i).text();
                String url = el.get(i).attr("href");
                if (url.startsWith("/")) {
                    url = WEBSITEURL + url;
                }
                mangaList.add(new TitleAndUrl(title, url));
            }
            return mangaList;
        } else {
            return null;
        }
    }

    private int getSearchTotalNum(String html) {
        Document doc = Jsoup.parse(html);
        Elements els = doc.select("#nav ul li");
        int index = els.size() - 2;
        return Integer.parseInt(els.get(index).text());
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
        } else {
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


}
