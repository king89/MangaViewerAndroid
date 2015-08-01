package com.king.mangaviewer;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.king.mangaviewer.actviity.MainActivity;
import com.king.mangaviewer.actviity.MyApplication;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<MyApplication> {
    public ApplicationTest() {
        super(MyApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //assertNotNull("Intent was null", null);

    }
}