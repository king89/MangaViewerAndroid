package com.king.mangaviewer.model;

import android.text.TextUtils;

import com.king.mangaviewer.util.StringUtils;

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

    public String getHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(menu.getHash() + "|" + this.getUrl());
        return StringUtils.getHash(sb.toString());
    }

    @Override
    public String getTitle() {
        String title = super.getTitle();
        if (!TextUtils.isEmpty(title)) {
            return title.replace(getMenu().getTitle(), "");
        }
        return super.getTitle();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MangaChapterItem) {
            return this.getHash().equals(((MangaChapterItem) obj).getHash());
        }
        return super.equals(obj);
    }
}
