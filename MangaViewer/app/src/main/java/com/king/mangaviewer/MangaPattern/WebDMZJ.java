package com.king.mangaviewer.MangaPattern;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.king.mangaviewer.model.TitleAndUrl;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 10/25/2016.
 */

public class WebDMZJ extends WebSiteBasePattern {
    String IMAGEURL = "http://images.dmzj.com/";
    public WebDMZJ(Context context) {
        super(context);
        WEBSITEURL = "http://manhua.dmzj.com/";
        CHARSET = "utf-8";
    }

    @Override
    public String getLatestMangaUrl() {
        return "http://manhua.dmzj.com/update_1.shtml";
    }

    @Override
    protected List<TitleAndUrl> getLatestMangaList(String html) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();
        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".boxdiv1");
        Iterator i = el.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = checkUrl(e.select(".pictextst ").attr("href"));
            String title = e.select(".pictextst").text();
            String imageUrl = e.select(".picborder a img").attr("src");
            topMangaList.add(new TitleAndUrl(title, url, imageUrl));
        }
        return topMangaList;
    }

    @Override
    public List<TitleAndUrl> getChapterList(String chapterUrl) {
        List<TitleAndUrl> list = new ArrayList<TitleAndUrl>();
        String html = getHtml(chapterUrl);
        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".cartoon_online_border ul li");
        Iterator i = el.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = checkUrl(e.select("a").attr("href"));
            String title = e.select("a").attr("title");
            String imageUrl = "";
            list.add(new TitleAndUrl(title, url, imageUrl));
        }
        return list;
    }

    @Override
    public List<String> getPageList(String firstPageUrl) {
        String html = getHtml(firstPageUrl);
        Pattern r = Pattern.compile("(eval\\(.+?\\[)(\".+?)(\\].+?,)(\\d+),(\\d+),(\'.+?\').+?(\\{\\}\\)\\))");
        try {
            Matcher m = r.matcher(html);
            m.find();
            String result = m.group(2);
            int num1 = Integer.parseInt(m.group(4));
            int num2 = Integer.parseInt(m.group(5));
            String strReplace = m.group(6);
            String[] names = strReplace.replace("'", "").split("\\|");

            List<String> list = Arrays.asList(result.split(","));
            String pattern = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (int i = 0; i < list.size(); i++) {
                String tmp = list.get(i).replace("\"", "").replace("\\\\", "");
                StringBuilder sb = new StringBuilder();
                for (int index = 0; index < tmp.length(); index++) {
                    if (pattern.contains(tmp.charAt(index) + "")) {
                        int replaceNum = GetInt(tmp.charAt(index) + "", num1, num2);
                        String replace = names[replaceNum];
                        if (replace.equals("")) {
                            replace = tmp.charAt(index)+"";
                        }
                        sb.append(replace);
                    }else {
                        sb.append(tmp.charAt(index));
                    }
                }
                list.set(i, IMAGEURL + sb.toString());
            }
            return list;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getImageUrl(String pageUrl, int nowPage) {
        return checkUrl(pageUrl);
    }

    private int GetInt(String s, int num1, int num2) {
        char[] aList = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        Map<String, Integer> aDict = new HashMap<String, Integer>();
        for (int i = 0; i < aList.length; i++) {
            aDict.put(aList[i] + "", i);
        }
        if (num2 < num1) {
            return 0;
        } else {
            char[] sArrary = s.toCharArray();
            double total = 0;
            int index = 0;
            for (int i = s.length() - 1; i >= 0; i--) {
                total = total + (Math.pow(num1, index) * aDict.get(sArrary[i] + ""));
                index += 1;
            }
            return (int) total;
        }
    }
}
