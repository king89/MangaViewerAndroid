package com.king.mangaviewer;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.king.mangaviewer.actviity.MainActivity;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.viewmodel.SettingViewModel;

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

        SettingHelper.saveSetting(mLaunchIntent, svm);

        svm = SettingViewModel.loadSetting(mLaunchIntent);

        assertNotNull(svm);
        assertNotNull(svm.getSelectedWebSite());
    }
}
