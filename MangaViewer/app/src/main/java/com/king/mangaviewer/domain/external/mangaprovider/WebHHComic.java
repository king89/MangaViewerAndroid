package com.king.mangaviewer.domain.external.mangaprovider;

import android.util.Log;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KinG on 12/24/2014.
 */
@Deprecated
public class WebHHComic extends MangaProvider {
    String LOG_TAG = "WebHHComic";
    String[] ServerList = new String[]{
            "http://216.18.193.164:9393/dm01/",
            "http://216.18.193.164:9393/dm02/",
            "http://216.18.193.164:9393/dm03/",
            "http://216.18.193.164:9393/dm04/",
            "http://216.18.193.164:9393/dm05/",
            "http://216.18.193.164:9393/dm06/",
            "http://216.18.193.164:9393/dm07/",
            "http://216.18.193.164:9393/dm08/",
            "http://216.18.193.164:9393/dm09/",
            "http://216.18.193.164:9393/dm10/",
            "http://216.18.193.164:9393/dm11/",
            "http://216.18.193.164:9393/dm12/",
            "http://216.18.193.164:9393/dm13/",
            "http://8.8.8.8:99/dm14/",
            "http://216.18.193.164:9393/dm15/",
            "http://216.18.193.164:9393/dm16/"};
    String code = "";
    String key = "tahfcioewrm";


    public WebHHComic() {
        // TODO Auto-generated constructor stub
        setWEBSITE_URL("http://www.hhxiee.cc/");
        setWEB_SEARCH_URL("http://somanhua.com/?key=%s&pageIndex=%d");
        setLatestMangaUrl("http://www.hhxiee.cc/");
        setWEB_ALL_MANGA_BASE_URL("http://www.hhxiee.cc/hhabc/");
        setCHARSET("gb2312");
    }

    private List<String> Decode(String code, String key, String server) {
        List<String> imgList = new ArrayList<String>();

        String spliter = key.substring(key.length() - 1);
        key = key.substring(0, key.length() - 1);
        for (int i = 0; i < key.length(); i++) {
            code = code.replace("" + key.charAt(i), "" + i);
        }
        String[] codeList = code.split(spliter);
        String result = "";
        for (String aCodeList : codeList) {
            result = result + (char) Integer.parseInt(aCodeList);
        }

        String[] resultList = result.split("\\|");
        String baseUrl = ServerList[Integer.parseInt(server) - 1];
        for (String aResultList : resultList) {
            imgList.add(baseUrl + aResultList);
        }
        return imgList;
    }

    private String GetServer(String url) {
        Pattern p = Pattern.compile("(?<=s=)[0-9]{1,2}");
        Matcher m = p.matcher(url);
        boolean b = m.find();
        return m.group();
    }

    @Override
    public List<String> getPageList(String firstPageUrl) {
        try {
            if (getFirstPageHtml() == null) {
                setFirstPageHtml(getHtml(firstPageUrl));
            }
            //Get code
            Pattern codeRe = Pattern.compile("(?<=PicListUrl = \")(.+?)(?=\")");
            Matcher m = codeRe.matcher(getFirstPageHtml());
            if (m.find()) {
                code = m.group(1);
                key = "tahfcioewrm";
            }
            String server = GetServer(firstPageUrl);
            List<String> pageList = Decode(code, key, server);

            return pageList;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getPageList", e.getMessage());
            return null;
        }
    }

    @Override
    public String getImageUrl(String pageUrl, int nowNum) {
        return pageUrl;
    }


    @Nullable @Override
    public List<TitleAndUrl> getChapterList(@NotNull MangaMenuItem menu) {
        String html = getHtml(menu.getUrl());
        //Rex1  = <ul class="mh_fj" .+<li>.+</li></ul>
        Pattern rGetUl = Pattern.compile("<ul class=\"bi\"[\\s\\S]+?</ul>");
        //Rex2 = <li>.*?</li>
        Matcher m = rGetUl.matcher(html);
        List<TitleAndUrl> chapterList = null;
        if (m.find()) {
            html = m.group(0);
            chapterList = new ArrayList<TitleAndUrl>();
            Pattern rUrlAndTitle = Pattern.compile("<li><a href=(.+?) .+?>(.+?)</a>");
            m = rUrlAndTitle.matcher(html);
            while (m.find()) {

                String url = m.group(1);
                //test has host or not
                if (url.startsWith("/")) {
                    url = getWEBSITE_URL() + url;
                }
                String title = m.group(2);
                chapterList.add(new TitleAndUrl(title, url));

            }
        }
        return chapterList;
    }

    @NotNull @Override
    protected List<MangaMenuItem> getLatestMangaList(@NotNull String html) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();
        try {
            Pattern rGetUl = Pattern
                    .compile("<div id=\"inhh\">[\\s\\S]+?<img src=\"(.+?)\" .+?>[\\s\\S]+?<a href=\"(.+?)\".+?>(.+?)</a>");
            Matcher m = rGetUl.matcher(html);
            while (m.find()) {
                String url = checkUrl(m.group(2));
                String title = m.group(3);
                String imageUrl = m.group(1);
                topMangaList.add(new TitleAndUrl(title, url, imageUrl));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.e(LOG_TAG, "getLatestMangaList");
        }
        return toMenuItem(topMangaList);
    }


    @Override
    protected List<TitleAndUrl> getSearchList(String html) {
        List<TitleAndUrl> mangaList = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Element el = doc.select(".dSHtm").get(0);
        for (int i = 0; i < el.children().size(); i++) {
            String title = el.child(i).select("a").first().text();
            String url = checkUrl(el.child(i).select("a").first().attr("href"));
            String imageUrl = el.child(i).select("a").first().select("img").attr("src");

            mangaList.add(new TitleAndUrl(title, url, imageUrl));
        }
        return mangaList;
    }

    @Override
    protected int getSearchTotalNum(String html) {
        Document doc = Jsoup.parse(html);
        String num = doc.select("#labPageCount").text();
        return Integer.parseInt(num);
    }


    @Override
    public List<TitleAndUrl> getAllMangaList(HashMap<String, Object> state) {
        List<TitleAndUrl> mangaList = new ArrayList<>();
        double mangaCountEachPage = 24.0f;

        String pageKey = state.containsKey(Companion.getSTATE_PAGE_KEY()) ? state.get(
                Companion.getSTATE_PAGE_KEY()).toString() : "a";
        int pageNum = state.containsKey(Companion.getSTATE_PAGE_NUM_NOW()) ? (int) state.get(
                Companion.getSTATE_PAGE_NUM_NOW()) : 1;
        int totalPageNumThisKey = state.containsKey(Companion.getSTATE_TOTAL_PAGE_NUM_THIS_KEY()) ?
                (int) state.get(Companion.getSTATE_TOTAL_PAGE_NUM_THIS_KEY()) : -1;
        boolean noMore = state.containsKey(Companion.getSTATE_NO_MORE()) ? (boolean) state.get(
                Companion.getSTATE_NO_MORE()) : false;

        if (!noMore) {
            //if totalPageNumThisKey == -1 means first time, then just go on
            if (totalPageNumThisKey != -1) {
                //increase page num
                if (pageNum + 1 <= totalPageNumThisKey) {
                    pageNum++;
                }
                //cant' increase page num, so increase key
                else {
                    pageKey = "" + ((char) (pageKey.charAt(0) + 1));
                    if (pageKey.charAt(0) > 'z') {
                        noMore = true;
                    }
                }
            }

            state.put(Companion.getSTATE_NO_MORE(), noMore);
            state.put(Companion.getSTATE_PAGE_KEY(), pageKey);
            state.put(Companion.getSTATE_PAGE_NUM_NOW(), pageNum);

            //start get html
            if (!noMore) {
                String trul = getWEB_ALL_MANGA_BASE_URL() + pageKey + "/" + pageNum + ".htm";
                String html = getHtml(trul);
                if (!html.isEmpty()) {

                    Document doc = Jsoup.parse(html);
                    Element el = doc.select(".replz").get(0);
                    //get total Page num
                    int totalMangaForThisKey = Integer.parseInt(el.select("font").get(0).text());
                    totalPageNumThisKey = (int) Math.ceil(totalMangaForThisKey / mangaCountEachPage);
                    state.put(Companion.getSTATE_TOTAL_PAGE_NUM_THIS_KEY(), totalPageNumThisKey);

                    //get manga list
                    el = doc.select(".list").get(0);
                    for (int i = 0; i < el.children().size(); i++) {
                        String title = el.child(i).select("a").text();
                        String url = checkUrl(el.child(i).select("a").attr("href"));
                        String imageUrl = el.child(i).select("img").attr("src");

                        mangaList.add(new TitleAndUrl(title, url, imageUrl));
                    }

                    return mangaList;
                } else {

                    return null;
                }
            }
        }
        return null;
    }
}
