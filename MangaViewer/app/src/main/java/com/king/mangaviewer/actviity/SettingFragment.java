package com.king.mangaviewer.actviity;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaMenuItemAdapter;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.List;


public class SettingFragment extends Fragment {

    private RadioGroup gv;
    private SettingViewModel sv;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        sv = ((MainActivity) this.getActivity()).getAppViewModel().Setting;
        gv = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        int checkedId = 0;
        for (int i = 0; i < Constants.WebSiteEnum.values().length; i++) {
            RadioButton b = new RadioButton(this.getActivity());
            b.setText(Constants.WebSiteEnum.values()[i].name());
            b.setTag(Constants.WebSiteEnum.values()[i]);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sv.setSelectedWebSite((Constants.WebSiteEnum) v.getTag());
                }
            });
            //checked
            if (sv.getSelectedWebSite() == Constants.WebSiteEnum.values()[i])
                checkedId = i;
            gv.addView(b);
        }
        ((RadioButton) gv.getChildAt(checkedId)).setChecked(true);
        return rootView;
    }
}