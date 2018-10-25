package com.king.mangaviewer.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.king.mangaviewer.model.HistoryMangaChapterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KinG on 9/10/2015.
 */
public class HistoryMangaDataSource extends MangaDataSourceBase {
    public final static String TABLE_NAME = "history_manga";
    //column
    //public static String ID = "id"; //different from the favourite manga item's id, this is the auto increment id
    public final static String HASH = "hash";
    public final static String VALUE = "value";
    public final static String UPDATED_DATE = "updated_date";

    public final static String[] All_COLUMN = {
            HASH,
            VALUE,
            UPDATED_DATE
    };

    public static String getCreateTableString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE " + TABLE_NAME);
        //builder.append(" ( " + ID + " integer, ");
        builder.append(" ( " + HASH + " text primary key, ");
        builder.append(VALUE + " text, ");
        builder.append(UPDATED_DATE + " text )");
        return builder.toString();
    }

    public HistoryMangaDataSource(Context context) {
        super(context);
    }

    //get top 100 history records
    public List<HistoryMangaChapterItem> getAllHistoryMangaItem() {

        List<HistoryMangaChapterItem> list = new ArrayList<>();
        Cursor cursor = null;
        String orderby = UPDATED_DATE + " desc";
        try {
            open();
            cursor = mDataBase.query(TABLE_NAME, All_COLUMN, null, null, null, null, orderby);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                HistoryMangaChapterItem item = cursorToItem(cursor);
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

    public void addToHistory(HistoryMangaChapterItem item) {
        String resultJson = new Gson().toJson(item, HistoryMangaChapterItem.class);
        ContentValues values = new ContentValues();
        try {
            //update or add
            if (checkIsExsit(item)) {
                open();
                String where = HASH + " = ? ";
                values.put(VALUE, resultJson);
                values.put(UPDATED_DATE, item.getLastReadDate());
                mDataBase.update(TABLE_NAME, values, where, new String[]{item.getHash()});
            } else {
                open();
                values.put(HASH, item.getHash());
                values.put(VALUE, resultJson);
                values.put(UPDATED_DATE, item.getLastReadDate());
                mDataBase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

    }

    private void deleteHistoryOver100(String date) {
        try {
            open();
            String where = UPDATED_DATE + " < ?";
            mDataBase.delete(TABLE_NAME, where, new String[]{date});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public boolean checkIsExsit(HistoryMangaChapterItem item) {
        String where = HASH + " = ? ";
        Cursor cursor = null;
        try {
            open();
            cursor = mDataBase.query(TABLE_NAME, All_COLUMN, where, new String[]{item.getHash()}, null, null, null);
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

    private void checkIfOver100() {

        List<HistoryMangaChapterItem> list = getAllHistoryMangaItem();
        if (list != null && list.size() > 100) {
            String date = list.get(99).getLastReadDate();
            deleteHistoryOver100(date);
        }

    }

    private HistoryMangaChapterItem cursorToItem(Cursor cursor) {
        //when reload, use hash for
        String value = cursor.getString(cursor.getColumnIndex(VALUE));
        HistoryMangaChapterItem item = new Gson().fromJson(value, HistoryMangaChapterItem.class);
        return item;
    }

    public void clearAll() {
        try {
            open();
            mDataBase.delete(TABLE_NAME, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }
}
