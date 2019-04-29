package com.king.mangaviewer.domain.external.mangaprovider;

import android.util.Log;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class WebIManhua extends MangaProvider {
    String LOG_TAG = "WebIManhua";
    String param = "p";
    String imagePrefix = ""; // imanhua_ , JOJO_, no prefix
    String imageFormat = ""; // jpg,png
    String imageUrl = "http://t5.mangafiles.com/Files/Images";
    ImanhuaInfo deserializedProduct = null;

    public WebIManhua() {
        // TODO Auto-generated constructor stub
        setWEBSITE_URL("http://www.imanhua.com/");
        setCHARSET("gb2312");
    }

    private int GetInt(String s, int isDecimal) {
        char[] aList = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        Map<String, Integer> aDict = new HashMap<String, Integer>();
        for (int i = 0; i < aList.length; i++) {
            aDict.put(aList[i] + "", i);
        }
        if (isDecimal == 10) {
            return Integer.parseInt(s);
        } else {
            char[] sArrary = s.toCharArray();
            double total = 0;
            int index = 0;
            for (int i = s.length() - 1; i >= 0; i--) {
                total = total + (Math.pow(isDecimal, index) * aDict.get(sArrary[i]));
                index += 1;
            }
            return (int) total;
        }
    }

    @NotNull @Override
    public List<MangaMenuItem> getLatestMangaList(@NotNull HashMap<String, Object> state) {
        // TODO Auto-generated method stub
        String html = getHtml(getLatestMangaUrl());
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();

        try {
            Pattern rGetUl = Pattern
                    .compile("(id=[\"']comicList[\"']>.+?</ul>)");
            Matcher m = rGetUl.matcher(html);
            m.find();
            html = m.group();
            Pattern rGetLi = Pattern.compile("(<li>.+?</li>)");
            m = rGetLi.matcher(html);
            Pattern rUrlAndTitle = Pattern
                    .compile("<a href=\"(.+?)\".+?title=\"(.+?)\"><img src=\"(.+?)\"");
            while (m.find()) {
                Matcher m2 = rUrlAndTitle.matcher(m.group());
                m2.find();
                String url = getWEBSITE_URL() + m2.group(1);
                String title = m2.group(2);
                String imageUrl = m2.group(3);
                topMangaList.add(new TitleAndUrl(title, url, imageUrl));

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            Log.e(LOG_TAG, "getLatestMangaList");
        }
        return toMenuItem(topMangaList);

    }

    @Nullable @Override
    public List<TitleAndUrl> getChapterList(@NotNull MangaMenuItem menu) {
        // TODO Auto-generated method stub
        // http://comic.131.com/content/shaonian/2104.html
        String html = getHtml(menu.getUrl());
        // Rex1 = <ul class="mh_fj" .+<li>.+</li></ul>
        Pattern rGetUl = Pattern.compile("id=[\"']subBookList[\"']>.+?</ul>");
        // Rex2 = <li>.*?</li>
        Matcher m = rGetUl.matcher(html);
        m.find();
        Pattern rGetLi = Pattern.compile("<li>.+?</li>");
        html = m.group(0);
        m = rGetLi.matcher(html);
        List<TitleAndUrl> chapterList = new ArrayList<TitleAndUrl>();
        Pattern rUrlAndTitle = Pattern.compile("<a href=\"(.+?)\".+?>(.+?)<");
        m = rUrlAndTitle.matcher(html);
        while (m.find()) {

            String url = getWEBSITE_URL() + m.group(1);
            String title = m.group(2);
            chapterList.add(new TitleAndUrl(title, url));

        }

        return chapterList;
    }

    @Override
    public List<String> getPageList(String firstPageUrl) {
        List<String> pageList = null;
        try {
            // TODO Auto-generated method stub
            if (getFirstPageHtml() == null) {
                setFirstPageHtml(getHtml(firstPageUrl));
            }
            setTotalNum(getTotalNum(getFirstPageHtml()));
            pageList = new ArrayList<String>();
            for (int i = getStartNum(); i <= getTotalNum(); i++) {
                pageList.add(firstPageUrl + "?" + param + "=" + i + "");
            }
            Pattern r = Pattern.compile("(?<=var cInfo=)\\{.+?\\}");
            Matcher m = r.matcher(getFirstPageHtml());
            if (m.find()) {
                String result = m.group();
                if (deserializedProduct == null) {
                    deserializedProduct = new ImanhuaInfo();
                }
                JSONObject jObject = new JSONObject(result);
                deserializedProduct.setObject(jObject);
            } else {
                r = Pattern.compile("(?<=}\\().+?(?=\\)\\))");
                m = r.matcher(getFirstPageHtml());

                if (m.find()) {
                    //从 js 函数取出 cinfo 代码 部分
                    r = Pattern.compile("(?<!\"'[^,]+),(?![^,]+\")");
                    String result = m.group();
                    String[] vars = r.split(result);
                    String cinfo = vars[0];
                    int isdecimal = Integer.parseInt(vars[1]);
                    String[] names = vars[3].replace(".split('|')", "")
                            .replaceAll("'$", "").split("|");
                    //解密cinfo
                    r = Pattern.compile("([0-9a-zA-Z]{1,3})");
                    m = r.matcher(cinfo);
                    //                MatchCInfo.names = names;
                    //                MatchCInfo.isdecimal = Int32.Parse(isdecimal);
                    StringBuffer sBuffer = new StringBuffer();

                    while (m.find()) {
                        m.appendReplacement(sBuffer,
                                names[GetInt(m.group(1), isdecimal)]);
                    }
                    m.appendTail(sBuffer);

                    r = Pattern.compile("(?<=var cInfo=){.+?}");
                    m = r.matcher(cinfo);

                    result = m.group();
                    if (deserializedProduct == null) {
                        deserializedProduct = new ImanhuaInfo();
                    }
                    JSONObject jObject = new JSONObject(result);
                    deserializedProduct.setObject(jObject);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return pageList;
    }

    public String getImageUrl(String pageUrl, int nowNum) {

        return imageUrl + '/' + deserializedProduct.bid + '/' + deserializedProduct.cid + '/' + deserializedProduct.files[nowNum];
    }
}

class ImanhuaInfo {
    public String bname;
    public int finished;
    public String burl;
    public String cname;
    public int cid;
    public int bid;
    public int len;
    public Object[] files;

    public void setObject(JSONObject obj) {
        try {
            this.bname = obj.getString("bname");
            this.finished = obj.getInt("finished");
            this.burl = obj.getString("burl");
            this.cname = obj.getString("cname");
            this.cid = obj.getInt("cid");
            this.bid = obj.getInt("bid");
            this.len = obj.getInt("len");
            //files
            JSONArray array = obj.getJSONArray("files");
            files = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                files[i] = (String) array.opt(i);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}