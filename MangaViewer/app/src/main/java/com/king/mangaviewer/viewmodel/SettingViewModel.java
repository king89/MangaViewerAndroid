package com.king.mangaviewer.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.king.mangaviewer.domain.external.mangaprovider.LocalMangaProvider;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.king.mangaviewer.R;
import com.king.mangaviewer.util.FileHelper;
import com.king.mangaviewer.util.SettingHelper;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SettingViewModel extends ViewModelBase {

    private MangaWebSource DEFAULT_WEB_SOURCE;
    private MangaWebSource mSelectedWebSource;
    private List<MangaWebSource> mMangaWebSources;
    private HashMap<String, FavouriteMangaMenuItem> mFavouriteMangaList;
    private FavouriteMangaDataSource mFavouriteMangaDataSource;
    private boolean mIsFromLeftToRight = true;
    private boolean mIsSplitPage = true;
    private Context context = null;
    private int mUpdatedFavouriteMangaCount;

    public SettingViewModel(Context context) {
        super(context);
        this.context = context;
    }

    public static SettingViewModel loadSetting(Context context) {
        SettingViewModel svm = new SettingViewModel(context);
        //Manga Sources
        svm.setMangaWebSources(loadMangaSource(context));

        //ensure get the latest manga source
        int id = svm.getSelectedWebSource().getId();
        svm.setSelectedWebSource(id, context);
        //if the previous web source deleted, get the first one
        if (svm.mSelectedWebSource == null) {
            svm.setSelectedWebSource(svm.mMangaWebSources.get(0).getId(), context);
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
        editor.apply();
        this.mIsSplitPage = mIsSplitPage;
    }

    public void setIsFromLeftToRight(Context context, boolean mIsFromLeftToRight) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(context.getString(R.string.pref_key_is_ltr), mIsFromLeftToRight);
        editor.apply();
        this.mIsFromLeftToRight = mIsFromLeftToRight;
    }

    public boolean getIsFromLeftToRight(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        mIsFromLeftToRight = sp.getBoolean(context.getString(R.string.pref_key_is_ltr), true);
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
                    String displayName = eElement.getElementsByTagName("DisplayName").item(
                            0).getTextContent();
                    String className = eElement.getElementsByTagName("ClassName").item(
                            0).getTextContent();
                    int order = Integer.parseInt(
                            eElement.getElementsByTagName("Order").item(0).getTextContent());
                    String language = eElement.getElementsByTagName("Language").item(
                            0).getTextContent();
                    int enable = Integer.parseInt(
                            eElement.getElementsByTagName("Enable").item(0).getTextContent());
                    //dont need the disable source
                    if (enable > 0) {
                        mws.add(new MangaWebSource(id, name, displayName, className, order,
                                language, enable));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //add local manga source
        Collections.sort(mws);
        mws.add(new MangaWebSource(-1, "LocalManga", "LocalManga", LocalMangaProvider.class.getName(), -1, "manga", 0));
        return mws;
    }

    public List<MangaWebSource> getMangaWebSources() {
        return mMangaWebSources;
    }

    public void setMangaWebSources(List<MangaWebSource> mMangaWebSources) {
        this.mMangaWebSources = mMangaWebSources;
        DEFAULT_WEB_SOURCE = mMangaWebSources.get(0);
    }

    //TODO need to fix
    public MangaWebSource getSelectedWebSource() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String id = sp.getString(context.getString(R.string.pref_key_manga_sources), null);
        if (id == null) {
            mSelectedWebSource = DEFAULT_WEB_SOURCE;
        } else {
            setSelectedWebSource(Integer.parseInt(id), context);
        }
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

    public String getCacheFolderSize(Context context) {
        String folder = context.getCacheDir().getPath();
        long size = FileHelper.getFileOrFolderSize(new File(folder));
        return FileHelper.calFileSize(size);
    }

    public void setSelectedWebSource(MangaWebSource webSite, Context context) {
        mSelectedWebSource = webSite;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(context.getString(R.string.pref_key_manga_sources),
                mSelectedWebSource.getId() + "");
        editor.apply();
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
        editor.commit();
    }

    public void saveSetting(Context context) {
        //SettingHelper.saveSetting(context, this);
        if (mFavouriteMangaDataSource != null) {
            mFavouriteMangaDataSource.close();
            mFavouriteMangaDataSource = null;
        }
    }

}
