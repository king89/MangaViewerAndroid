package com.king.mangaviewer;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.king.mangaviewer.ui.main.MainActivity;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.ArrayList;
import java.util.List;

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

//    @MediumTest
//    public void testSaveAndLoadSetting() {
//        mLaunchIntent = (MainActivity) (getActivity());
//        SettingViewModel svm = SettingViewModel.loadSetting(mLaunchIntent);
//
//        svm.addFavouriteManga(new FavouriteMangaMenuItem(new MangaMenuItem("1", "1", "1", "1", "url", svm.getSelectedWebSource(mLaunchIntent)), 0));
//        SettingHelper.saveSetting(mLaunchIntent, svm);
//
//        svm = SettingViewModel.loadSetting(mLaunchIntent);
//
//        assertNotNull(svm);
//        assertNotNull(svm.getSelectedWebSource(mLaunchIntent));
//    }

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

    @MediumTest
    public void testDataBaseLoadAndSaveFavouriteManga() {

        mLaunchIntent = getActivity();
        FavouriteMangaDataSource dataSource = new FavouriteMangaDataSource(getActivity());

        SettingViewModel svm = SettingViewModel.loadSetting(mLaunchIntent);
        List<MangaWebSource> sources = svm.getMangaWebSources();
        dataSource.getAllFavouriteMangaMenu(sources);
        List<FavouriteMangaMenuItem> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            FavouriteMangaMenuItem item = new FavouriteMangaMenuItem(new MangaMenuItem(i + "", i + "", i + "", i + "", "url" + i, svm.getSelectedWebSource(mLaunchIntent)), 0);
            list.add(item);
        }

        dataSource.addToFavourite(list.get(0));
        dataSource.addToFavourite(list.get(1));
        dataSource.addToFavourite(list.get(2));
        dataSource.addToFavourite(list.get(3));

        dataSource.removeFromFavourite(list.get(1));

        FavouriteMangaMenuItem item = list.get(2);
        item.setUpdateCount(2);
        dataSource.updateToFavourite(item);


    }
}
