package com.king.mangaviewer.activity;


import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.viewmodel.HistoryViewModel;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    protected SettingViewModel mSettingViewModel;
    protected MangaViewModel mMangaViewModel;
    private InitContentAsyc mInitContentAsyc;
    public BaseFragment() {
        // Required empty public constructor
    }
    protected InitContentAsyc getInitContentAsycExcutor(){
        return new InitContentAsyc();
    }
    protected SettingViewModel getSettingViewModel(){
        return ((BaseActivity)this.getActivity()).getAppViewModel().Setting;
    }

    protected MangaViewModel getMangaViewModel(){
        return ((BaseActivity)this.getActivity()).getAppViewModel().Manga;
    }
    protected HistoryViewModel getHistoryViewModel(){
        return ((BaseActivity)this.getActivity()).getAppViewModel().HistoryManga;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);

        return textView;
    }


    public void refresh(){}

    protected class InitContentAsyc extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return getContentBackground();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateContent();
        }
    }

    protected void updateContent() {
    }

    protected Void getContentBackground() {
        return null;
    }
}
