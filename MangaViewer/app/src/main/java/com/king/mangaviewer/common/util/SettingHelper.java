package com.king.mangaviewer.common.util;

import com.king.mangaviewer.common.Constants.WebSiteEnum;

public class SettingHelper {

    WebSiteEnum webType;

    public WebSiteEnum getWebType() {
        return WebSiteEnum.HHComic;
    }

    public void setWebType(WebSiteEnum webType) {
        this.webType = webType;
    }
}
