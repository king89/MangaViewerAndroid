package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.model.TitleAndUrl;

import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 9/2/2015.
 */
public class WebMangaReader extends WebSiteBasePattern{

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
    public List<TitleAndUrl> getLatestMangaList(HashMap<String, Object> state) {
        return super.getLatestMangaList(state);
    }
}
