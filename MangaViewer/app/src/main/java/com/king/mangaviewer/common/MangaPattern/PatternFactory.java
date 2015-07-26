package com.king.mangaviewer.common.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.common.Constants.WebSiteEnum;

public class PatternFactory {
    public static WebSiteBasePattern getPattern(Context context, WebSiteEnum type) {
        try {
            return (WebSiteBasePattern) Class.forName(type.getClsName()).getConstructor(Context.class).newInstance(context);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }
}
