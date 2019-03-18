package com.king.mangaviewer.datasource;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by KinG on 9/10/2015.
 */
@Deprecated
public class MangaDataSourceBase {
    protected final static String DB_NAME = "manga.db";
    protected final static int DB_VERSION = 1;
    protected MangaDataSQLHelper mDBHelper = null;
    protected SQLiteDatabase mDataBase = null;

    MangaDataSourceBase(Context context) {
        mDBHelper = new MangaDataSQLHelper(context);
    }

    static private class MangaDataSQLHelper extends SQLiteOpenHelper {
        private static final String CREATE_FAVOURITE_MANGA_TABLE = FavouriteMangaDataSource.getCreateTableString();
        private static final String CREATE_HISTORY_MANGA_TABLE = HistoryMangaDataSource.getCreateTableString();
        private static final String DROP_FAVOURITE_MANGA_TABLE =
                "DROP TABLE IF EXISTS " + FavouriteMangaDataSource.FAVOURITE_MANGA_TABLE;
        public MangaDataSQLHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_FAVOURITE_MANGA_TABLE);
            db.execSQL(CREATE_HISTORY_MANGA_TABLE);
            Log.i("MangaDataSourceBase", "onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_FAVOURITE_MANGA_TABLE);
            onCreate(db);
        }
    }

    public MangaDataSourceBase open() throws SQLException {
        if (mDataBase == null) {
            this.mDataBase = mDBHelper.getWritableDatabase();
        }
        return this;
    }

    public void close() {
        this.mDataBase = null;
        mDBHelper.close();
    }
}
