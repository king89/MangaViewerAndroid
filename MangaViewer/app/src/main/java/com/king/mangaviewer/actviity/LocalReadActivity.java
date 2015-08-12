package com.king.mangaviewer.actviity;

import android.os.Bundle;
import android.view.animation.AnimationUtils;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.component.MangaImageSwitcher;

import java.util.ArrayList;

/**
 * Created by KinG on 7/15/2015.
 */
public class LocalReadActivity extends BaseActivity {

    ArrayList<String> fl = new ArrayList<String>();
    private String filePath;
    private MangaImageSwitcher mis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitvity_local_read);

        mis = (MangaImageSwitcher) this.findViewById(R.id.mangaImageSwitcher);
        filePath = getAppViewModel().LoacalManga.getSelectedFilePath();
        mis.Initial(filePath);
        mis.setInAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in));
        mis.setOutAnimation(AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out));
    }

    @Override
    protected boolean IsCanBack() {
        return true;
    }

}