package com.king.mangaviewer.domain.data.mangaprovider;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by KinG on 9/2/2015.
 */
public class WebMangaReader extends MangaProvider {

    private final static int PAGE_SIZE = 30;
    public static final int SEARCH_LIST_PAGE_SIZE = 30;

    public WebMangaReader() {
        WEBSITE_URL = "http://www.mangareader.net/";
        WEB_LATEST_MANGA_BASE_URL = "http://www.mangareader.net/";
        WEB_SEARCH_URL = "http://www.mangareader.net/search/?w=%s&rd=0&status=0&order=0&p=%d";
        WEB_ALL_MANGA_BASE_URL = "http://www.mangareader.net/popular/%d";
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
    public List<TitleAndUrl> getChapterList(String chapterUrl) {
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
    public List<String> getPageList(String firstPageUrl) {
        List<String> pageList = new ArrayList<>();
        String html = getHtml(firstPageUrl);
        int total = getTotalNum(html);

        for (int i = 1; i <= total; i++) {
            pageList.add(firstPageUrl + "/" + i);
        }
        return pageList;
    }

    @Override
    public int getTotalNum(String html) {
        Document doc = Jsoup.parse(html);
        return doc.select("#pageMenu option").size();
    }

    @Override
    public String getImageUrl(String pageUrl, int nowPage) {
        String html = getHtml(pageUrl);
        Document doc = Jsoup.parse(html);
        return doc.select("#img").attr("src");
    }

    @Override
    protected String getSearchUrl(String queryText, int pageNum) {
        return super.getSearchUrl(queryText, (pageNum - 1) * SEARCH_LIST_PAGE_SIZE);
    }

    @Override
    protected int getSearchTotalNum(String html) {
        try {
            Document doc = Jsoup.parse(html);
            String t = doc.select("#sp a").last().attr("href");
            t = t.substring(t.lastIndexOf("=") + 1);
            int num = (int) Math.ceil(Integer.parseInt(t) / (SEARCH_LIST_PAGE_SIZE * 1.0f)) + 1;
            return num;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected List<TitleAndUrl> getSearchList(String html) {
        List<TitleAndUrl> mangaList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".mangaresultitem a");

        for (int i = 0; i < el.size(); i++) {
            String title = el.get(i).text();
            String url = checkUrl(el.get(i).attr("href"));
            mangaList.add(new TitleAndUrl(title, url));
        }
        return mangaList;

    }


    @Override
    protected int getAllMangaTotalNum(String html) {
        try {
            Document doc = Jsoup.parse(html);
            String t = doc.select("#sp a").last().attr("href");
            t = t.substring(t.lastIndexOf("/") + 1);
            int num = (int) Math.ceil(Integer.parseInt(t) / (SEARCH_LIST_PAGE_SIZE * 1.0f)) + 1;
            return num;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  0;
    }

    @Override
    protected List<TitleAndUrl> getAllMangaList(String html) {
        return getSearchList(html);
    }

    @Override
    protected String getAllMangaUrl(int pageNum) {
        return super.getAllMangaUrl((pageNum - 1) * SEARCH_LIST_PAGE_SIZE);
    }
}
