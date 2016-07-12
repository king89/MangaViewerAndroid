package com.king.mangaviewer.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.HistoryChapterItemAdapter;
import com.king.mangaviewer.model.HistoryMangaChapterItem;

import java.util.List;


public class HistoryFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private TextView tv;
    private List<HistoryMangaChapterItem> dateList = null;

    public HistoryFragment() {
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        //Toast.makeText(getActivity(),"OnResume",Toast.LENGTH_SHORT);
        getInitContentAsycExcutor().execute();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.history_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            new AlertDialog.Builder(this.getActivity())
                    .setTitle(getString(R.string.msg_history_dialog_title))
                    .setMessage(getString(R.string.msg_history_dialog_message))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getHistoryViewModel().clearHistory();
                            onResume();
                        }

                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        tv = (TextView) rootView.findViewById(R.id.textView);

        getInitContentAsycExcutor().execute();
        return rootView;
    }

    @Override
    protected Void getContentBackground() {
        getHistoryMangaList();
        return null;
    }

    @Override
    protected void updateContent() {
        super.updateContent();
        MainActivity activity = (MainActivity) getActivity();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.Adapter adapter = new HistoryChapterItemAdapter(activity, activity.getAppViewModel().Manga, dateList);
        recyclerView.setAdapter(adapter);
        tv.setVisibility(View.GONE);
        if (dateList != null && dateList.size() == 0) {
            tv.setText(getString(R.string.history_no_history_manga));
            tv.setVisibility(View.VISIBLE);
        }
    }

    private void getHistoryMangaList() {
        MainActivity activity = (MainActivity) getActivity();
        dateList = activity.getAppViewModel().HistoryManga.getHistoryChapterList();


    }
}