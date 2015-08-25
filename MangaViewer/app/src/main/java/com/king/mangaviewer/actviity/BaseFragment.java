package com.king.mangaviewer.actviity;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private SettingViewModel mSettingViewModel;
    private MangaViewModel mMangaViewModel;
    public BaseFragment() {
        // Required empty public constructor
    }

    protected SettingViewModel getSettingViewModel(){
        return ((BaseActivity)this.getActivity()).getAppViewModel().Setting;
    }

    protected MangaViewModel getMangaViewModel(){
        return ((BaseActivity)this.getActivity()).getAppViewModel().Manga;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);
        return textView;
    }


}
