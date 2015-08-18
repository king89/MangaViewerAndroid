package com.king.mangaviewer.common.util;

import android.util.Log;

import com.king.mangaviewer.common.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {
    public static String saveFile(String folderPath, String fileName,
                                  InputStream data) {

        try {

            File dir = new File(folderPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir.getAbsolutePath() + File.separator
                    + fileName);
            file.createNewFile();
            if (file.exists() && file.canWrite()) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = data.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }

                } catch (Exception e) {
                    Log.e(Constants.LOGTAG, "ERROR", e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            // swallow
                        }
                    }
                }
            }
            return file.getAbsolutePath();

        } catch (Exception e) {
            // TODO: handle exception
            Log.e(Constants.LOGTAG, "error saveFile", e);

        }
        return null;
    }

    public static String saveFile(String folderPath, String fileName,
                                  byte[] data) {
        try {

            File dir = new File(folderPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir.getAbsolutePath() + File.separator
                    + fileName);
            file.createNewFile();
            if (file.exists() && file.canWrite()) {
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    fos.write(data);
                } catch (Exception e) {
                    Log.e(Constants.LOGTAG, "ERROR", e);
                } finally {
                    if (fos != null) {
                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            // swallow
                        }
                    }
                }
            }
            return file.getAbsolutePath();

        } catch (Exception e) {
            // TODO: handle exception
            Log.e(Constants.LOGTAG, "error saveFile", e);

        }
        return null;
    }

    public static byte[] loadFile(String folderPath, String fileName) {
        String filePath = folderPath + File.separator + fileName;
        return loadFile(filePath);
    }

    public static byte[] loadFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.canRead()) {
            BufferedInputStream fis = null;
            try {
                fis = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = null;
                buffer = new byte[(int) file.length()];
                fis.read(buffer);
                return buffer;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } else {
            return null;
        }

    }

    public static String getFileName(String str) {
        return str.substring(str.lastIndexOf(File.separator) + 1);
    }

    public static String concatPath(String path1, String path2) {
        String result = path1;
        if (path1.substring(path1.length()-1) == File.separator) {
            result = path1 + path2;
        } else {
            result = result + File.separator + path2;
        }

        return result;
    }

    public static void serializeObject(String filePath, Object item) {

    }

    public static Object deserializeObject(String filePath) {
        return null;

    }

    public static void resetFolder(String folderPath){
        File folder = new File(folderPath);
        if (folder.exists()){
            deleteRecursive(folder);
        }
        folder = new File(folderPath);
        folder.mkdirs();
    }

    private static  void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    public static long getFileOrFolderSize(File dir) {

        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for(int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if(fileList[i].isDirectory()) {
                    result += getFileOrFolderSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }

    public static String calFileSize(long size) {
        //use MB for unit
        int s = (int) Math.ceil(size / 1024.0f / 1024.0f);
        return s + " MB";
    }
}
