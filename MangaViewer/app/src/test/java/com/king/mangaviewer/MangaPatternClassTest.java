package com.king.mangaviewer;

import android.content.Context;

import com.king.mangaviewer.domain.data.mangaprovider.MangaProvider;
import com.king.mangaviewer.domain.data.mangaprovider.WebDMZJ;
import com.king.mangaviewer.model.TitleAndUrl;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by liang on 10/25/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MangaPatternClassTest  {

   @Mock
   Context mMockContext;
    @Test
    public void getMangaList(){

        MangaProvider wbp = new WebDMZJ(mMockContext);
        HashMap<String, Object> hashMap = new HashMap<>();
        List<TitleAndUrl> list = wbp.getLatestMangaList(hashMap);
        System.out.println(list.get(0).getUrl());
        assertTrue(list.size() > 0);
    }

    @Test
    public void getChapterList(){
        MangaProvider wbp = new WebDMZJ(mMockContext);
        String url = "http://manhua.dmzj.com/gsmmx21/";
        List<TitleAndUrl> list = wbp.getChapterList(url);
        System.out.println(list.get(0).getUrl());
        assertTrue(list.size() > 0);
    }

    @Test
    public void getPageList(){
        MangaProvider wbp = new WebDMZJ(mMockContext);
        String url = "http://manhua.dmzj.com/gsmmx21/3.shtml";
        List<String> list = wbp.getPageList(url);

        assertTrue(list.size() > 0);
        System.out.println(list.get(0));

    }

    @Test
    public void searchManga(){


    }
}
