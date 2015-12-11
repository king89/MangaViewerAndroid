package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;
import android.util.Log;

import com.king.mangaviewer.model.TitleAndUrl;

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
public class WebHHComic extends WebSiteBasePattern {
    String LOG_TAG = "WebHHComic";
    String[] ServerList = new String[]{
            "http://img2.hhcomic.net/dm01/",
            "http://img2.hhcomic.net/dm02/",
            "http://img2.hhcomic.net/dm03/",
            "http://img2.hhcomic.net/dm04/",
            "http://img2.hhcomic.net/dm05/",
            "http://img2.hhcomic.net/dm06/",
            "http://img2.hhcomic.net/dm07/",
            "http://img2.hhcomic.net/dm08/",
            "http://img2.hhcomic.net/dm09/",
            "http://img2.hhcomic.net/dm10/",
            "http://img2.hhcomic.net/dm11/",
            "http://img2.hhcomic.net/dm12/",
            "http://img2.hhcomic.net/dm13/",
            "http://8.8.8.8:99/dm14/",
            "http://img2.hhcomic.net/dm15/",
            "http://img2.hhcomic.net/dm16/"};
    String code = "";
    String key = "tazsicoewrm";


    public WebHHComic(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        WEBSITEURL = "http://www.hhxiee.cc/";
        WEBSEARCHURL = "http://somanhua.com/?key=%s&pageIndex=%d";
        WEBLATESTMANGABASEURL = "http://www.hhxiee.cc/";
        WEBALLMANGABASEURL = "http://www.hhxiee.cc/hhabc/";
        CHARSET = "gb2312";
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
    public List<String> GetPageList(String firstPageUrl) {
        try {
            if (firstPageHtml == null) {
                firstPageHtml = getHtml(firstPageUrl);
            }
            //Get code
            Pattern codeRe = Pattern.compile("(?<=PicListUrl = \")(.+?)(?=\")");
            Matcher m = codeRe.matcher(firstPageHtml);
            if (m.find()) {
                code = m.group(1);
                key = "tahfcioewrm";
            }
            String server = GetServer(firstPageUrl);
            List<String> pageList = Decode(code, key, server);

            return pageList;
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("GetPageList",e.getMessage());
            return null;
        }
    }

    @Override
    public String GetImageUrl(String pageUrl, int nowNum) {
        return pageUrl;
    }


    @Override
    public List<TitleAndUrl> GetChapterList(String chapterUrl) {
        String html = getHtml(chapterUrl);
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
                    url = WEBSITEURL + url;
                }
                String title = m.group(2);
                chapterList.add(new TitleAndUrl(title, url));

            }
        }
        return chapterList;
    }

    @Override
    protected List<TitleAndUrl> getLatestMangaList(String html) {
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
        return topMangaList;
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

        String pageKey = state.containsKey(STATE_PAGE_KEY) ? state.get(STATE_PAGE_KEY).toString() : "a";
        int pageNum = state.containsKey(STATE_PAGE_NUM_NOW) ? (int) state.get(STATE_PAGE_NUM_NOW) : 1;
        int totalPageNumThisKey = state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY) ?
                (int) state.get(STATE_TOTAL_PAGE_NUM_THIS_KEY) : -1;
        boolean noMore = state.containsKey(STATE_NO_MORE) ? (boolean) state.get(STATE_NO_MORE) : false;

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

            state.put(STATE_NO_MORE, noMore);
            state.put(STATE_PAGE_KEY, pageKey);
            state.put(STATE_PAGE_NUM_NOW, pageNum);

            //start get html
            if (!noMore) {
                String trul = WEBALLMANGABASEURL + pageKey + "/" + pageNum + ".htm";
                String html = getHtml(trul);
                if (!html.isEmpty()) {

                    Document doc = Jsoup.parse(html);
                    Element el = doc.select(".replz").get(0);
                    //get total Page num
                    int totalMangaForThisKey = Integer.parseInt(el.select("font").get(0).text());
                    totalPageNumThisKey = (int) Math.ceil(totalMangaForThisKey / mangaCountEachPage);
                    state.put(STATE_TOTAL_PAGE_NUM_THIS_KEY, totalPageNumThisKey);

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
