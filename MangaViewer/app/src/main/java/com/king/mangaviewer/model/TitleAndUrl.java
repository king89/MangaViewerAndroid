package com.king.mangaviewer.model;

public class TitleAndUrl {

    private String Title;
    private String Url;
    private String ImagePath;

    public TitleAndUrl(String title, String url, String imagePath) {
        Title = title;
        Url = url;
        ImagePath = imagePath;
    }

    public TitleAndUrl(String title, String url) {
        Title = title;
        Url = url;
        ImagePath = "";
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }
}
