package com.king.mangaviewer.MangaPattern;

import android.content.Context;

import com.king.mangaviewer.model.MangaWebSource;

public class PatternFactory {
    public static WebSiteBasePattern getPattern(Context context, MangaWebSource type) {
        try {
            return (WebSiteBasePattern) Class.forName(type.getClassName()).getConstructor(Context.class).newInstance(context);
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
