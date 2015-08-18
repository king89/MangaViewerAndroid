package com.king.mangaviewer;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.king.mangaviewer.actviity.MainActivity;
import com.king.mangaviewer.actviity.MangaPageActivity;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.common.util.FileHelper;
import com.king.mangaviewer.common.util.SettingHelper;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.io.File;

/**
 * Created by KinG on 7/31/2015.
 */
public class MangaPageMouldeTest extends ActivityInstrumentationTestCase2<MangaPageActivity> {

    private MangaPageActivity mLaunchIntent;

    public MangaPageMouldeTest() {
        super(MangaPageActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @MediumTest
    public void testGoNextPage() {

    }

}
