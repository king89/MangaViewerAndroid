package com.king.mangaviewer.viewmodel;

public class LocalMangaViewModel extends ViewModelBase {

    private String selectedFilePath;

    public String getSelectedFilePath() {
        return selectedFilePath;
    }

    public void setSelectedFilePath(String path) {
        selectedFilePath = path;
    }
}
