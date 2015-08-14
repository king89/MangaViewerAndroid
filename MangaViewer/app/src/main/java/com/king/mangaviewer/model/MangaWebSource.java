package com.king.mangaviewer.model;

/**
 * Created by KinG on 8/13/2015.
 */
public class MangaWebSource implements Comparable<MangaWebSource>{

    int id;
    String name;
    String displayName;
    String className;
    int order;
    String language;
    int enable;

    public MangaWebSource(int id,
                          String name,
                          String displayName,
                          String className,
                          int order,
                          String language,
                          int enable) {
        this.name = name;
        this.displayName = displayName;
        this.className = className;
        this.order = order;
        this.language = language;
        this.id = id;
        this.enable = enable;
    }

    public String getClassName() {
        return className;
    }

    public int getOrder() {
        return order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(MangaWebSource another) {
        if (this.getOrder() < another.getOrder()){
            return -1;
        }else{
            return 1;
        }
    }


}
