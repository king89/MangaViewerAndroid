package com.king.mangaviewer.util;

import android.content.Context;
import android.os.Environment;
import com.king.mangaviewer.common.Constants;
import java.io.File;

public class SettingHelper {
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
}
