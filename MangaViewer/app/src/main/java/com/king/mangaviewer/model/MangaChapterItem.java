package com.king.mangaviewer.model;

public class MangaChapterItem extends BaseItem {
    MangaMenuItem menu = null;

    /**
     * @param id
     * @param title
     * @param description
     * @param imagePath
     * @param menu
     */

    public MangaChapterItem(String id, String title, String description,
                            String imagePath, String url, MangaMenuItem menu) {
        super(id, title, description, imagePath, url, menu.getMangaWebSource());
        this.menu = menu;
    }


    public MangaMenuItem getMenu() {
        return menu;
    }


    public void setMenu(MangaMenuItem menu) {
        this.menu = menu;
    }
}
