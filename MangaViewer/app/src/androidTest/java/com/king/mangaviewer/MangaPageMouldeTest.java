package com.king.mangaviewer;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.king.mangaviewer.actviity.MainActivity;
import com.king.mangaviewer.common.MangaPattern.WebHHComic;
import com.king.mangaviewer.common.util.MangaHelper;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by KinG on 7/31/2015.
 */
public class MangaPageMouldeTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mLaunchIntent;

    public MangaPageMouldeTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

//    @MediumTest
//    public void testGetAllMangaList() {
//        SettingViewModel sv = getActivity().getAppViewModel().Setting;
//        sv.setSelectedWebSource(sv.getMangaWebSources().get(0),getActivity());
//        List<MangaMenuItem> mMangaList = new ArrayList<>();
//        HashMap<String, Object> mStateHash = new HashMap<>();
//        MangaHelper helper = new MangaHelper(getActivity());
//
//        mStateHash.put(WebHHComic.STATE_PAGE_KEY, "z");
//        mStateHash.put(WebHHComic.STATE_PAGE_NUM_NOW, 66);
//        boolean isFinished = false;
//        while (!isFinished) {
//            for (int i = 0; i < 10; i++) {
//                helper.getAllManga(mMangaList, mStateHash);
//                String key = (String) mStateHash.get(WebHHComic.STATE_PAGE_KEY);
//                int totalNum = (int) mStateHash.get(WebHHComic.STATE_TOTAL_PAGE_NUM_THIS_KEY);
//                int nowPage = (int) mStateHash.get(WebHHComic.STATE_PAGE_NUM_NOW);
//
//
//                if (mStateHash.containsKey(WebHHComic.STATE_NO_MORE) && ((boolean)mStateHash.get(WebHHComic.STATE_NO_MORE)) == true) {
//                    isFinished = true;
//                    break;
//                }
//
//                Log.i("Test", "key:" + key + " totalNum:" + totalNum + " nowPage:" + nowPage + " listSize:" + mMangaList.size());
//            }
//            isFinished = true;
//        }
//
//    }

}
