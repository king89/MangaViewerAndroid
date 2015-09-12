package com.king.mangaviewer.viewmodel;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.util.FileHelper;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SettingViewModel extends ViewModelBase {

    private MangaWebSource mSelectedWebSource;
    private List<MangaWebSource> mMangaWebSources;
    private HashMap<String, FavouriteMangaMenuItem> mFavouriteMangaList;
    private FavouriteMangaDataSource mFavouriteMangaDataSource;
    private boolean mIsFromLeftToRight = true;
    private boolean mIsSplitPage = true;

    private int mUpdatedFavouriteMangaCount;

    public SettingViewModel(Context context) {
        mContext = context;
    }

    public static SettingViewModel loadSetting(Context context) {
        SettingViewModel svm = new SettingViewModel(context);
        //Manga Sources
        svm.setMangaWebSources(loadMangaSource(context));

        if (svm.mSelectedWebSource == null) {
            svm.mSelectedWebSource = svm.mMangaWebSources.get(0);
        } else {
            //ensure get the latest manga source
            int id = svm.mSelectedWebSource.getId();
            svm.setSelectedWebSource(id, context);
        }
        return svm;
    }

    private FavouriteMangaDataSource getFavouriteMangaDataSource() {
        if (mFavouriteMangaDataSource == null) {
            mFavouriteMangaDataSource = new FavouriteMangaDataSource(mContext);
        }
        return mFavouriteMangaDataSource;
    }

    ;

    public void setIsSplitPage(Context context, boolean mIsSplitPage) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_key_split_page), mIsSplitPage);
        editor.commit();
        this.mIsSplitPage = mIsSplitPage;
    }

    public void setIsFromLeftToRight(boolean mIsFromLeftToRight) {
        this.mIsFromLeftToRight = mIsFromLeftToRight;
    }

    public boolean getIsFromLeftToRight() {
        return mIsFromLeftToRight;
    }

    public boolean getIsSplitPage(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        mIsSplitPage = sp.getBoolean(context.getString(R.string.pref_key_split_page), true);
        return mIsSplitPage;
    }

    private static List<MangaWebSource> loadMangaSource(Context context) {
        List<MangaWebSource> mws = new ArrayList<MangaWebSource>();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.manga_source);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Source");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    int id = Integer.parseInt(eElement.getAttribute("id"));
                    String name = eElement.getElementsByTagName("Name").item(0).getTextContent();
                    String displayName = eElement.getElementsByTagName("DisplayName").item(0).getTextContent();
                    String className = eElement.getElementsByTagName("ClassName").item(0).getTextContent();
                    int order = Integer.parseInt(eElement.getElementsByTagName("Order").item(0).getTextContent());
                    String language = eElement.getElementsByTagName("Language").item(0).getTextContent();
                    int enable = Integer.parseInt(eElement.getElementsByTagName("Enable").item(0).getTextContent());

                    mws.add(new MangaWebSource(id, name, displayName, className, order, language, enable));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // mws.add(new MangaWebSource(3, "manga3", "manga3", "com.king.mangaviewer.common.MangaPattern.WebHHComic", 2, "manga", 1));
        Collections.sort(mws);
        return mws;
    }

    public List<MangaWebSource> getMangaWebSources() {
        return mMangaWebSources;
    }

    public void setMangaWebSources(List<MangaWebSource> mMangaWebSources) {
        this.mMangaWebSources = mMangaWebSources;
    }

    public boolean checkIsFavourited(MangaMenuItem manga) {
        return getFavouriteMangaDataSource().checkIsexsit(new FavouriteMangaMenuItem(manga));
    }

    public boolean addFavouriteManga(MangaMenuItem manga, int chapterCount) {
        if (checkIsFavourited(manga)) {
            return false;
        } else {
            getFavouriteMangaDataSource().addToFavourite(new FavouriteMangaMenuItem(manga, chapterCount));
            return true;
        }

    }

    public boolean addFavouriteManga(FavouriteMangaMenuItem manga) {
        return addFavouriteManga(manga, 0);
    }

    public boolean removeFavouriteManga(MangaMenuItem manga) {
        if (checkIsFavourited(manga)) {
            getFavouriteMangaDataSource().removeFromFavourite(new FavouriteMangaMenuItem(manga));
            return true;
        } else {
            return false;
        }
    }

    public List<FavouriteMangaMenuItem> getFavouriteMangaList() {
        return getFavouriteMangaDataSource().getAllFavouriteMangaMenu(getMangaWebSources());
    }

    public MangaWebSource getSelectedWebSource(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String id = sp.getString(context.getString(R.string.pref_key_manga_sources), "0");
        setSelectedWebSource(Integer.parseInt(id), context);
        return mSelectedWebSource;
    }

    public void resetMangaFolder(Context context) {
        String folder = SettingHelper.getMangaFolder(context);
        FileHelper.resetFolder(folder);
    }

    public String getMangaFolderSize(Context context) {
        String folder = SettingHelper.getMangaFolder(context);
        long size = FileHelper.getFileOrFolderSize(new File(folder));
        return FileHelper.calFileSize(size);
    }

    public void setSelectedWebSource(MangaWebSource webSite, Context context) {
        mSelectedWebSource = webSite;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_key_manga_sources), mSelectedWebSource.getId() + "");
        editor.commit();
    }

    public void setSelectedWebSource(int id, Context context) {
        mSelectedWebSource = null;
        for (MangaWebSource m : mMangaWebSources) {
            if (id == m.getId()) {
                setSelectedWebSource(m, context);
            }
        }

    }

    public String getDefaultLocalMangaPath(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(context.getString(R.string.pref_key_default_path), "");
    }

    public void setDefaultLocalMangaPath(Context context, String path) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_key_default_path), path);
    }

    public void saveSetting(Context context) {
        //SettingHelper.saveSetting(context, this);
        if (mFavouriteMangaDataSource != null) {
            mFavouriteMangaDataSource.close();
            mFavouriteMangaDataSource = null;
        }
    }

    public int getUpdatedFavouriteMangaCount() {
        return mUpdatedFavouriteMangaCount;
    }

    public void setUpdatedFavouriteMangaCount(int updatedFavouriteMangaCount) {
        this.mUpdatedFavouriteMangaCount = updatedFavouriteMangaCount;
    }
}
