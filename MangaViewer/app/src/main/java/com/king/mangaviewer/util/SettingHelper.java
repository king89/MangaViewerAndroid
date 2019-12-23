package com.king.mangaviewer.util;

import android.content.Context;
import android.os.Environment;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.Constants.WebSiteEnum;
import java.io.File;

public class SettingHelper {

    private static final String SETTINGFILE = "setting";
    WebSiteEnum webType;
    private WebSiteEnum mTest = WebSiteEnum.HHComic;

//    public static boolean saveSetting(Context context, SettingViewModel setting) {
//        String folderName = getSettingFolder(context);
  //        String fileUri = SETTINGFILE;
//        String ss = new Gson().toJson(setting);
//
//        InputStream inputStream = new ByteArrayInputStream(ss.getBytes());
  //        FileHelper.saveFile(folderName, fileUri, inputStream);
//        return true;
//    }

//    public static SettingViewModel loadSetting(Context context) {
//        String folderName = getSettingFolder(context);
  //        String fileUri = SETTINGFILE;
  //
  //        byte[] data = FileHelper.loadFile(folderName, fileUri);
//        SettingViewModel tmp = null;
//        if (data != null) {
//            String ss = new String(data);
//            try {
//                tmp = new Gson().fromJson(ss, SettingViewModel.class);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (tmp == null)
//            {
//                tmp = new SettingViewModel();
//            }
//        } else {
//            tmp = new SettingViewModel();
//        }
//
//        return tmp;
//    }


    public static String getSettingFolder(Context context) {
        //TODO
        // Check if have external storage
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            return context.getExternalFilesDir(null) + File.separator
                    + Constants.INSTANCE.getSETTINGFOLDER();
        } else {
            return context.getFilesDir() + File.separator
                    + Constants.INSTANCE.getSETTINGFOLDER();
        }
    }

    public static String getMangaFolder(Context context) {
        // Check if have external storage
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            return context.getExternalFilesDir(null) + File.separator
                    + Constants.INSTANCE.getMANGAFOLDER();
        } else {
            return context.getFilesDir() + File.separator
                    + Constants.INSTANCE.getMANGAFOLDER();
        }
    }

    public WebSiteEnum getWebType() {
        return WebSiteEnum.HHComic;
    }

    public void setWebType(WebSiteEnum webType) {
        this.webType = webType;
    }
}
