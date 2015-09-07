package com.king.mangaviewer.common.util;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.king.mangaviewer.actviity.MyApplication;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.Constants.WebSiteEnum;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SettingHelper {

    private static final String SETTINGFILE = "setting";
    WebSiteEnum webType;
    private WebSiteEnum mTest = WebSiteEnum.HHComic;

    public static boolean saveSetting(Context context, SettingViewModel setting) {
        String folderName = getSettingFolder(context);
        String fileName = SETTINGFILE;
        String ss = new Gson().toJson(setting);

        InputStream inputStream = new ByteArrayInputStream(ss.getBytes());
        FileHelper.saveFile(folderName, fileName, inputStream);
        return true;
    }

    public static SettingViewModel loadSetting(Context context) {
        String folderName = getSettingFolder(context);
        String fileName = SETTINGFILE;

        byte[] data = FileHelper.loadFile(folderName, fileName);
        SettingViewModel tmp = null;
        if (data != null) {
            String ss = new String(data);
            try {
                tmp = new Gson().fromJson(ss, SettingViewModel.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (tmp == null)
            {
                tmp = new SettingViewModel();
            }
        } else {
            tmp = new SettingViewModel();
        }

        return tmp;
    }


    public static String getSettingFolder(Context context) {
        //TODO
        // Check if have external storage
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            return context.getExternalFilesDir(null) + File.separator
                    + Constants.SETTINGFOLDER;
        } else {
            return context.getFilesDir() + File.separator
                    + Constants.SETTINGFOLDER;
        }
    }

    public static String getMangaFolder(Context context) {
        // Check if have external storage
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            return context.getExternalFilesDir(null) + File.separator
                    + Constants.MANGAFOLDER;
        } else {
            return context.getFilesDir() + File.separator
                    + Constants.MANGAFOLDER;
        }
    }

    public WebSiteEnum getWebType() {
        return WebSiteEnum.HHComic;
    }

    public void setWebType(WebSiteEnum webType) {
        this.webType = webType;
    }
}
