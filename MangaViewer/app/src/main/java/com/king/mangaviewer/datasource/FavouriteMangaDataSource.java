package com.king.mangaviewer.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 9/10/2015.
 */
public class FavouriteMangaDataSource extends MangaDataSourceBase {
    public static String FAVOURITE_MANGA_TABLE = "favourite_manga";

    //column
    //public static String ID = "id"; //different from the favourite manga item's id, this is the auto increment id
    public final static String HASH = "hash";
    public final static String TITLE = "title";
    public final static String DESCRIPTION = "description";
    public final static String IMAGEPATH = "imagePath";
    public final static String URL = "url";
    public final static String MANGAWEBSOURCE_ID = "manga_websource_id";
    public final static String FAVOURITE_DATE = "favourite_date";
    public final static String UPDATED_DATE = "updated_date";
    public final static String UPDATE_COUNT = "update_count";
    public final static String CHAPTER_COUNT = "chapter_count";

    public final static String[] All_COLUMN = {
            //ID,
            HASH,
            TITLE,
            DESCRIPTION,
            IMAGEPATH,
            URL,
            MANGAWEBSOURCE_ID,
            FAVOURITE_DATE,
            UPDATED_DATE,
            UPDATE_COUNT,
            CHAPTER_COUNT
    };

    public static String getCreateTableString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE " + FAVOURITE_MANGA_TABLE);
        //builder.append(" ( " + ID + " integer, ");
        builder.append(" ( " + HASH + " text  primary key, ");
        builder.append(TITLE + " text, ");
        builder.append(DESCRIPTION + " text, ");
        builder.append(IMAGEPATH + " text, ");
        builder.append(URL + " text, ");
        builder.append(MANGAWEBSOURCE_ID + " integer, ");
        builder.append(FAVOURITE_DATE + " text, ");
        builder.append(UPDATED_DATE + " text, ");
        builder.append(UPDATE_COUNT + " integer, ");
        builder.append(CHAPTER_COUNT + " integer ) ");

        return builder.toString();
    }

    public FavouriteMangaDataSource(Context context) {
        super(context);
    }

    public List<FavouriteMangaMenuItem> getAllFavouriteMangaMenu(List<MangaWebSource> mangaWebSources) {

        List<FavouriteMangaMenuItem> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            open();
            cursor = mDataBase.query(FAVOURITE_MANGA_TABLE, All_COLUMN, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                FavouriteMangaMenuItem item = cursorToItem(cursor, mangaWebSources);
                if (item != null) {
                    list.add(item);
                }
                cursor.moveToNext();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return list;
    }

    public void addToFavourite(FavouriteMangaMenuItem menu) {
        ContentValues values = new ContentValues();
        values.put(HASH, menu.getHash());
        values.put(TITLE, menu.getTitle());
        values.put(DESCRIPTION, menu.getDescription());
        values.put(IMAGEPATH, menu.getImagePath());
        values.put(URL, menu.getUrl());
        values.put(MANGAWEBSOURCE_ID, menu.getMangaWebSource().getId());
        values.put(FAVOURITE_DATE, menu.getFavouriteDate());
        values.put(UPDATED_DATE, menu.getUpdatedDate());
        values.put(UPDATE_COUNT, menu.getUpdateCount());
        values.put(CHAPTER_COUNT, menu.getChapterCount());
        try {
            open();
            mDataBase.insertWithOnConflict(FAVOURITE_MANGA_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    public void removeFromFavourite(FavouriteMangaMenuItem menu) {
        String where = HASH + " = ? ";

        try {
            open();
            mDataBase.delete(FAVOURITE_MANGA_TABLE, where, new String[]{menu.getHash()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void updateToFavourite(FavouriteMangaMenuItem menu) {
        String where = HASH + " = ? ";
        ContentValues values = new ContentValues();
        values.put(TITLE, menu.getTitle());
        values.put(DESCRIPTION, menu.getDescription());
        values.put(IMAGEPATH, menu.getImagePath());
        values.put(URL, menu.getUrl());
        values.put(MANGAWEBSOURCE_ID, menu.getMangaWebSource().getId());
        values.put(UPDATED_DATE, menu.getUpdatedDate());
        values.put(UPDATE_COUNT, menu.getUpdateCount());
        values.put(CHAPTER_COUNT, menu.getChapterCount());
        try {
            open();
            mDataBase.update(FAVOURITE_MANGA_TABLE, values, where, new String[]{menu.getHash()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public boolean checkIsexsit(FavouriteMangaMenuItem menu) {
        String where = HASH + " = ? ";
        Cursor cursor = null;
        try {
            open();
            cursor = mDataBase.query(FAVOURITE_MANGA_TABLE, All_COLUMN, where, new String[]{menu.getHash()}, null, null, null);
            if (cursor != null) {
                return cursor.getCount() > 0 ? true : false;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return true;
    }

    private FavouriteMangaMenuItem cursorToItem(Cursor cursor, List<MangaWebSource> mangaWebSources) {
        //when reload, use hash for
        String hash = cursor.getString(cursor.getColumnIndex(HASH));
        String title = cursor.getString(cursor.getColumnIndex(TITLE));
        String description = cursor.getString(cursor.getColumnIndex(DESCRIPTION));
        String imagePath = cursor.getString(cursor.getColumnIndex(IMAGEPATH));
        String url = cursor.getString(cursor.getColumnIndex(URL));
        int chapterCount = cursor.getInt(cursor.getColumnIndex(CHAPTER_COUNT));
        int updateCount = cursor.getInt(cursor.getColumnIndex(UPDATE_COUNT));
        String favouriteDate = cursor.getString(cursor.getColumnIndex(FAVOURITE_DATE));
        String updateDate = cursor.getString(cursor.getColumnIndex(UPDATED_DATE));
        int mangaWebSourceId = cursor.getInt(cursor.getColumnIndex(MANGAWEBSOURCE_ID));
        MangaWebSource mangaWebSource = null;
        for (MangaWebSource m : mangaWebSources) {
            if (m.getId() == mangaWebSourceId) {
                mangaWebSource = m;
                break;
            }
        }
        if (mangaWebSource == null) {
            return null;
        }

        MangaMenuItem menu = new MangaMenuItem(hash, title, description, imagePath, url, mangaWebSource);
        return FavouriteMangaMenuItem.Companion.createFavouriteMangaMenuItem(menu, favouriteDate, updateDate, chapterCount, updateCount);
    }

}
