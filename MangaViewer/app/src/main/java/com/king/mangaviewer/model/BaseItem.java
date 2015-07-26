package com.king.mangaviewer.model;

import java.io.Serializable;

public class BaseItem implements Serializable {
    String id = "";
    String title = "";
    String description = "";
    String imagePath = "";
    String url = "";

    /**
     * @param id
     * @param title
     * @param description
     * @param imagePath
     */

    public BaseItem(String id, String title, String description,
                    String imagePath, String url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
