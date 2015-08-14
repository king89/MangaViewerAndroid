package com.king.mangaviewer.viewmodel;


import android.content.Context;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.model.MangaWebSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SettingViewModel extends ViewModelBase {

    private MangaWebSource mSelectedWebSource;
    private String mDefaultLocalMangaPath;
    private List<MangaWebSource> mMangaWebSources;

    public static SettingViewModel loadSetting(Context context) {
        SettingViewModel svm = SettingHelper.loadSetting(context);

        svm.mMangaWebSources = loadMangaSource(context);

        if (svm.mSelectedWebSource == null) {
            svm.mSelectedWebSource = svm.mMangaWebSources.get(0);
        } else {
            //ensure get the latest manga source
            svm.mSelectedWebSource = svm.mMangaWebSources.get(svm.mSelectedWebSource.getId());
        }

        if (svm.mDefaultLocalMangaPath == null) {
            svm.mDefaultLocalMangaPath = "";
        }

        return svm;
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

                    mws.add(new MangaWebSource(id,name,displayName,className,order,language,enable));
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

    public MangaWebSource getSelectedWebSource() {
        return mSelectedWebSource;
    }

    public void setSelectedWebSource(MangaWebSource webSite) {
        mSelectedWebSource = webSite;
    }

    public String getDefaultLocalMangaPath() {
        return mDefaultLocalMangaPath;
    }

    public void setDefaultLocalMangaPath(String path) {
        mDefaultLocalMangaPath = path;
    }

    public void saveSetting(Context context) {
        SettingHelper.saveSetting(context, this);
    }
}
