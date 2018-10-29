package com.king.mangaviewer.domain.data.mangaprovider;

import com.king.mangaviewer.model.MangaWebSource;

public class ProviderFactory {
    public static MangaProvider getPattern(MangaWebSource type) {
        try {
            return (MangaProvider) Class.forName(type.getClassName()).newInstance();
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
