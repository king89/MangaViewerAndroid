package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;
import android.os.Environment;

import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.Constants.SaveType;
import com.king.mangaviewer.common.util.FileHelper;
import com.king.mangaviewer.common.util.StringUtils;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.model.TitleAndUrl;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebSiteBasePattern {
    public String WEBSITEURL = "";
    public String WEBSEARCHURL = "";
    public String WEBALLMANGABASEURL = "";
    public String WEBLATESTMANGABASEURL = "";
    public String CHARSET = "utf8";
    protected int startNum = 1;
    protected int totalNum = 1;
    protected String firstPageHtml = null;
    private Context context;

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
        String test = "http://i1.imanhua.com/Cover/2011-10/sishen.jpg";
        return test;
    }

    // public void GetImageByImageUrl(MangaPageItem page,SaveType saveType) {
    // return ; }
    public int InitSomeArgs(String firstPageUrl) {
        return 0;
    }

    // public void DownloadOnePage(String pageUrl,String folder,int nowPageNum)
    // { return; }
    public String getHtml(String Url) {
        URL url;
        try {
            url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

    public String getLatestMangaHtml(){
        return getHtml(this.WEBSITEURL);
    }
    public String getAllMangaHtml(){
        return getHtml(this.WEBALLMANGABASEURL);
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
    public List<TitleAndUrl> getLatestMangaList(String html) {
        return null;
    }

    public List<TitleAndUrl> GetNewMangaList(String html) {
        return null;
    }

    public List<TitleAndUrl> GetSearchingList(String queryText, int pageNum) {
        return null;
    }

    public List<TitleAndUrl> getAllManga(HashMap<String, Object> state) {
        return null;
    }

    public String getMenuCover(MangaMenuItem menu) {
        return menu.getImagePath();
    }
}
