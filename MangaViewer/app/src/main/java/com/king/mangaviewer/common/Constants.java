package com.king.mangaviewer.common;

import com.king.mangaviewer.domain.data.mangaprovider.WebHHComic;
import com.king.mangaviewer.domain.data.mangaprovider.WebIManhua;
import com.king.mangaviewer.domain.data.mangaprovider.WebTestManga;

public class Constants {
    public final static String MANGAFOLDER = "Manga";
    public final static String SETTINGFOLDER = "Setting";
    public final static String LOGTAG = "MangaViewer";

    public static enum MSGType {
        Menu,
        Chapter,
        Page;
    }

    public enum WebSiteEnum {

        IManhua(WebIManhua.class.getName(), 0),
        HHComic(WebHHComic.class.getName(), 1),
        TestManga(WebTestManga.class.getName(), 2);
        //        Local();

        private String clsName;
        private int index;

        private WebSiteEnum(String cls, int index) {
            this.clsName = cls;
            this.index = index;
        }

        public String getClsName() {
            return this.clsName;
        }

        public int getIndex() {
            return index;
        }

    }

    public enum SaveType {
        Temp,
        Local
    }
}
