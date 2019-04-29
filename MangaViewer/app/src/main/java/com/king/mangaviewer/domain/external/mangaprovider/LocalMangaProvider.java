package com.king.mangaviewer.domain.external.mangaprovider;

import android.util.Log;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaUriType;
import com.king.mangaviewer.model.TitleAndUrl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

/**
 * Created by KinG on 12/24/2014.
 */
public class LocalMangaProvider extends MangaProvider {
    String LOG_TAG = "LocalManga";

    @Inject
    public LocalMangaProvider() {

        // TODO Auto-generated constructor stub
        setWEBSITE_URL("");
        setWEB_SEARCH_URL("");
        setCHARSET("utf8");
    }


    @Override
    public List<String> getPageList(String firstPageUrl) {

        List<String> fileList = new ArrayList<String>();
        try {
            ZipEntry ze = null;
            ZipFile zp = new ZipFile(firstPageUrl);
            Enumeration<? extends ZipEntry> it = zp.entries();
            while (it.hasMoreElements()) {
                ze = it.nextElement();
                fileList.add(ze.getName());
                Log.v("loadManga", "" + ze.getSize());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileList;
    }


    @Override
    public String getImageUrl(String pageUrl, int nowNum) {
        return pageUrl;
    }


    @Override
    public List<TitleAndUrl> getChapterList(String chapterUrl) {
        File path = new File(chapterUrl);
        List<String> fileList = null;
        List<TitleAndUrl> chapterList = new ArrayList<TitleAndUrl>();
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(LOG_TAG, "unable to write on the sd card ");
        }
        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return sel.isFile() && sel.getName().contains(".zip") && !sel.isHidden();

                }
            };

            String[] fList = path.list(filter);
            fileList = new ArrayList<>();
            for (int i = 0; i < fList.length; i++) {
                fileList.add(fList[i]);
                // Convert into file path
                File sel = new File(path, fList[i]);

                chapterList.add(new TitleAndUrl(sel.getName(), sel.getAbsolutePath()));
            }

            Collections.sort(chapterList);

        }
        return chapterList;
    }


    @NotNull @Override
    public List<MangaMenuItem> getLatestMangaList(@NotNull HashMap<String, Object> state) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();

        for (int i = 0; i < 10; i++) {
            String url = getWEBSITE_URL() + i;
            String title = "Test Menu " + i;
            String imageUrl = "";
            topMangaList.add(new TitleAndUrl(title, url, imageUrl));

        }

        return toMenuItem(topMangaList);
    }

    @NotNull @Override public MangaUriType getLoaderType() {
        return MangaUriType.ZIP;
    }
}
