package com.king.mangaviewer.actviity;

import android.os.Bundle;
import android.os.Message;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.component.MyViewFlipper;

public class MangaPageActivity extends BaseActivity {

    MyViewFlipper vFlipper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected String getActionBarTitle() {
        // TODO Auto-generated method stub
        return this.getAppViewModel().Manga.getSelectedMangaChapterItem().getTitle();
    }

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub

        setContentView(R.layout.activity_manga_page);
        vFlipper = (MyViewFlipper) this.findViewById(R.id.viewFlipper);
        vFlipper.initial(getAppViewModel().Manga, getAppViewModel().Setting, handler, false);

    }

    @Override
    protected void update(Message msg) {
        this.getSupportActionBar().setTitle(getActionBarTitle());
    }

    @Override
    protected void goBack() {
        getAppViewModel().Manga.setNowPagePosition(0);
        getAppViewModel().Manga.setMangaPageList(null);
        super.goBack();
    }


    @Override
    protected boolean IsCanBack() {
        // TODO Auto-generated method stub
        return true;
    }

}
