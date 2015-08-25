package com.king.mangaviewer;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.king.mangaviewer.actviity.MainActivity;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.util.FileHelper;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.io.File;

/**
 * Created by KinG on 7/31/2015.
 */
public class SettingMouldeTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mLaunchIntent;

    public SettingMouldeTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @MediumTest
    public void testSaveAndLoadSetting() {
        mLaunchIntent = (MainActivity) (getActivity());
        SettingViewModel svm = SettingViewModel.loadSetting(mLaunchIntent);

        svm.addFavouriteManga(new FavouriteMangaMenuItem(new MangaMenuItem("1", "1", "1", "1", "url", svm.getSelectedWebSource(mLaunchIntent))));
        SettingHelper.saveSetting(mLaunchIntent, svm);

        svm = SettingViewModel.loadSetting(mLaunchIntent);

        assertNotNull(svm);
        assertNotNull(svm.getSelectedWebSource(mLaunchIntent));
    }

//    @MediumTest
//    public void testResetFolder(){
//        mLaunchIntent = (MainActivity) (getActivity());
//        File folder = mLaunchIntent.getFilesDir();
//        String mangaFolder = FileHelper.concatPath(folder.getAbsolutePath(), Constants.MANGAFOLDER);
//
//        String s = new SettingViewModel().getMangaFolderSize(mLaunchIntent);
//
//        Log.v("testResetFolder",s);
//    }
}
