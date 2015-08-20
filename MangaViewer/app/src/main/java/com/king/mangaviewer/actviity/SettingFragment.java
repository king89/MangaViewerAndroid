package com.king.mangaviewer.actviity;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.common.Constants;
import com.king.mangaviewer.viewmodel.SettingViewModel;


public class SettingFragment extends Fragment {

    private RadioGroup gv;
    private SettingViewModel sv;

    public SettingFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        sv = ((MainActivity) this.getActivity()).getAppViewModel().Setting;
        gv = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        int checkedId = 0;
        for (int i = 0; i < sv.getMangaWebSources().size(); i++) {
            RadioButton b = new RadioButton(this.getActivity());
            b.setText(sv.getMangaWebSources().get(i).getDisplayName());
            b.setTag(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sv.setSelectedWebSource(sv.getMangaWebSources().get((int)v.getTag()));
                }
            });
            //checked
            if (sv.getSelectedWebSource().getId() == sv.getMangaWebSources().get(i).getId())
                checkedId = i;
            gv.addView(b);
        }
        ((RadioButton) gv.getChildAt(checkedId)).setChecked(true);

        //Folder size Part
        final TextView tv = (TextView)rootView.findViewById(R.id.folderSizeTextView);
        tv.setText(sv.getMangaFolderSize(this.getActivity()));

        Button bt = (Button)rootView.findViewById(R.id.clear_data_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.resetMangaFolder(getActivity());
                tv.setText(sv.getMangaFolderSize(SettingFragment.this.getActivity()));
            }
        });

        return rootView;
    }
}