package com.king.mangaviewer.MangaPattern;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.Constants.SaveType;
import com.king.mangaviewer.util.FileHelper;
import com.king.mangaviewer.util.StringUtils;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.model.TitleAndUrl;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSiteBasePattern {
    private final static String LOG_TAG = "WebSiteBasePattern";
    private static final int HTTP_TIMEOUT_NUM = 10000;
    public String WEBSITEURL = "";
    public String WEBSEARCHURL = "";
    public String WEBALLMANGABASEURL = "";
    public String WEBLATESTMANGABASEURL = "";
    public String CHARSET = "utf8";
    protected int startNum = 1;
    protected int totalNum = 1;
    protected String firstPageHtml = null;
    private Context context;

    public static final String STATE_SEARCH_QUERYTEXT = "key_search_querytext";
    public final static String STATE_PAGE_KEY = "key_page_key";
    public final static String STATE_PAGE_NUM_NOW = "key_page_num_now";
    public final static String STATE_TOTAL_PAGE_NUM_THIS_KEY = "key_total_page_num";
    public final static String STATE_NO_MORE = "key_no_more";


    public WebSiteBasePattern(Context context) {
        this.context = context;
    }

    public List<String> GetPageList(String firstPageUrl) {
        List<String> list = new ArrayList<String>();
        String prefix = "http://www.imanhua.com/comic/1067/list_104097.html?p=";
        for (int i = 0; i < 10; i++) {
            list.add(prefix + i);
        }
        return list;
    }

    public int GetTotalNum(String html) {
        Pattern r = Pattern.compile("value=\"[0-9]+\"");
        Matcher m = r.matcher(html);

        Pattern r2 = Pattern.compile("[0-9]+");

        Matcher m2 = null;
        int max = -9;
        while (m.find()) {
            String tmp = m.group();
            m2 = r2.matcher(tmp);
            m2.find();
            int now = Integer.parseInt(m2.group());
            if (max < now) {
                max = now;
            }

        }

        if (max > 0) {
            return max;
        } else {
            return 0;
        }

    }

    public String GetImageUrl(String pageUrl) {
        return null;
    }

    public String GetImageUrl(String pageUrl, int nowPage) {
        return pageUrl;
    }

    // public void GetImageByImageUrl(MangaPageItem page,SaveType saveType) {
    // return ; }
    public int InitSomeArgs(String firstPageUrl) {
        return 0;
    }

    protected String checkUrl(String url) {
        if (url.startsWith("/")) {
            url = WEBSITEURL + url.substring(1);
        }
        //remove last "/"
        if (url.endsWith("/") && url.length() > 1) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    // public void DownloadOnePage(String pageUrl,String folder,int nowPageNum)
    // { return; }
    public String getHtml(String Url) {
        URL url;
        try {
            url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HTTP_TIMEOUT_NUM);
            conn.setReadTimeout(HTTP_TIMEOUT_NUM);
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            String html = StringUtils.inputStreamToString2(inputStream,
                    this.CHARSET);
            return html;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }

    public String getLatestMangaUrl() {
        return this.WEBLATESTMANGABASEURL;
    }

    public String getMangaFolder() {
        if (context != null) {
            // Check if have external storage
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                return context.getExternalFilesDir(null) + File.separator
                        + Constants.MANGAFOLDER + File.separator
                        + this.getClass().getSimpleName();
            } else {
                return context.getFilesDir() + File.separator
                        + Constants.MANGAFOLDER + File.separator
                        + this.getClass().getSimpleName();
            }
        } else {
            return null;
        }

    }

    public String getPrePageImageFilePath(String imgUrl, MangaPageItem pageItem) {
        String folderName = getMangaFolder() + File.separator
                + pageItem.getFolderPath();
        String fileName = FileHelper.getFileName(imgUrl);
        File dir = new File(folderName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir.getAbsolutePath() + File.separator
                + fileName);
        return file.getAbsolutePath();
    }


    public String DownloadImgPage(String imgUrl, MangaPageItem pageItem,
                                  SaveType saveType, String refer) {
        if (refer == null || refer == "") {
            refer = this.WEBSITEURL;
        }
        String folderName = getMangaFolder() + File.separator
                + pageItem.getFolderPath();
        String fileName = FileHelper.getFileName(imgUrl);
        try {

            String UserAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.56 Safari/536.5";
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpMethod = new HttpGet(imgUrl);
            httpMethod.addHeader("Referer", refer);
            httpMethod.addHeader("User-Agent", UserAgent);
            HttpEntity entity = client.execute(httpMethod).getEntity();

            InputStream inputStream = entity.getContent();

            return FileHelper.saveFile(folderName, fileName, inputStream);

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
     * // // Chapter //
     */
    public List<TitleAndUrl> GetChapterList(String chapterUrl) {
        return null;
    }

    /*
     * // // Menu //
     */
    public List<TitleAndUrl> getLatestMangaList(HashMap<String, Object> state) {
        String html = getHtml(getLatestMangaUrl());
        if (html == null || html.isEmpty()) {
            return null;
        }
        state.put(this.STATE_NO_MORE,true);
        return getLatestMangaList(html);
    }

    protected List<TitleAndUrl> getLatestMangaList(String html) {
        return null;
    }

    public List<TitleAndUrl> GetNewMangaList(String html) {
        return null;
    }

    /*Searching Manga*/
    public List<TitleAndUrl> getSearchingList(HashMap<String, Object> state) {
        boolean noMore = false;
        if (state.containsKey(STATE_NO_MORE)) {
            noMore = (boolean) state.get(STATE_NO_MORE);
        }

        if (!noMore) {
            String queryText = state.get(STATE_SEARCH_QUERYTEXT).toString();
            try {
                queryText = java.net.URLEncoder.encode(queryText, CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int pageNum = 1;
            int totalNum = state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY) ? (int) state.get(STATE_TOTAL_PAGE_NUM_THIS_KEY) : 0;
            String html = "";
            //no total num means first time
            if (state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY)) {
                if (state.containsKey(STATE_PAGE_NUM_NOW)) {
                    pageNum = (int) state.get(STATE_PAGE_NUM_NOW);
                }
                if (pageNum + 1 <= totalNum) {
                    pageNum++;
                    state.put(STATE_PAGE_NUM_NOW, pageNum);
                } else {
                    state.put(STATE_NO_MORE, true);
                    return null;
                }
            }

            String turl = getSearchUrl(queryText, pageNum);
            Log.v(LOG_TAG, "Search: " + turl);
            html = getHtml(turl);
            if (html.isEmpty()) {
                return null;
            }
            if (totalNum == 0) {
                totalNum = getSearchTotalNum(html);
            }
            state.put(STATE_TOTAL_PAGE_NUM_THIS_KEY, totalNum);
            return getSearchList(html);
        } else {
            return null;
        }
    }

    protected int getSearchTotalNum(String html) {
        return 0;
    }

    protected String getSearchUrl(String queryText, int pageNum) {
        return String.format(WEBSEARCHURL, queryText, pageNum);
    }

    protected List<TitleAndUrl> getSearchList(String html) {
        return null;
    }
    /*Searching Manga*/

    /*AllManga*/
    public List<TitleAndUrl> getAllMangaList(HashMap<String, Object> state) {
        boolean noMore = false;
        if (state.containsKey(STATE_NO_MORE)) {
            noMore = (boolean) state.get(STATE_NO_MORE);
        }

        if (!noMore) {
            int pageNum = 1;
            int totalNum = state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY) ? (int) state.get(STATE_TOTAL_PAGE_NUM_THIS_KEY) : 0;
            String html = "";
            //no total num means first time
            if (state.containsKey(STATE_TOTAL_PAGE_NUM_THIS_KEY)) {
                if (state.containsKey(STATE_PAGE_NUM_NOW)) {
                    pageNum = (int) state.get(STATE_PAGE_NUM_NOW);
                }
                if (pageNum + 1 <= totalNum) {
                    pageNum++;
                    state.put(STATE_PAGE_NUM_NOW, pageNum);
                } else {
                    state.put(STATE_NO_MORE, true);
                    return null;
                }
            }

            String turl = getAllMangaUrl(pageNum);
            Log.v(LOG_TAG, "Search: " + turl);
            html = getHtml(turl);
            if (html.isEmpty()) {
                return null;
            }
            if (totalNum == 0) {
                totalNum = getAllMangaTotalNum(html);
            }
            state.put(STATE_TOTAL_PAGE_NUM_THIS_KEY, totalNum);
            return getAllMangaList(html);
        } else {
            return null;
        }
    }

    protected List<TitleAndUrl> getAllMangaList(String html) {
        return null;
    }

    protected int getAllMangaTotalNum(String html) {
        return 0;
    }

    protected String getAllMangaUrl(int pageNum) {
        return String.format(WEBALLMANGABASEURL, pageNum);
    }
    /*AllManga*/


    public String getMenuCover(MangaMenuItem menu) {
        return menu.getImagePath();
    }
}
