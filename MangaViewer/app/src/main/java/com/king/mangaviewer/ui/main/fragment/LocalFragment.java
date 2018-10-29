package com.king.mangaviewer.ui.main.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.base.BaseActivity;
import com.king.mangaviewer.base.BaseFragment;
import com.king.mangaviewer.activity.LocalReadActivity;
import com.king.mangaviewer.adapter.LocalFileItemAdapter;

import com.king.mangaviewer.ui.main.MainActivity;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LocalFragment extends BaseFragment {

    private static final String TAG = "F_PATH";
    // Stores names of traversed directories
    ArrayList<String> str = new ArrayList<String>();
    LocalFileItemAdapter adapter;
    // Check if the first level of the directory structure is the one showing
    private Boolean firstLvl = true;
    private RecyclerView recyclerView;
    private List<Item> fileList;
    private File path;
    private String chosenFile;
    private String extraPath;
    LocalFileItemAdapter.OnLocalFileItemClickListener listener;
    TextView tv;

    public LocalFragment() {
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

        String defalutPaht = ((MainActivity) this.getActivity()).getAppViewModel().Setting.getDefaultLocalMangaPath(getActivity());
        //TODO
        path = new File(Environment.getExternalStorageDirectory() + defalutPaht);
        extraPath = defalutPaht;
        String[] childFolders = extraPath.split("/");
        for (String s : childFolders) {
            if (!s.isEmpty()) {
                str.add(s);
            }
        }
        View rootView = inflater.inflate(R.layout.fragment_local, container, false);
        tv = (TextView) rootView.findViewById(R.id.textView);
        Button bt = (Button) rootView.findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity activity = ((BaseActivity) getActivity());
                if (activity != null) {
                    activity.getAppViewModel().Setting.setDefaultLocalMangaPath(getActivity(), extraPath);
                    Toast.makeText(getActivity(), getString(R.string.local_set_default_path_successed), Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new LocalFileItemAdapter(getContext(), null, null));
        tv.setText(extraPath);
        startAsyncTask();
        //showDialog(DIALOG_LOAD_FILE);
        Log.d(TAG, path.getAbsolutePath());

        return rootView;
    }

    @Override
    protected Void getContentBackground() {

        loadFileList();

        return super.getContentBackground();
    }

    @Override
    protected void updateContent() {
        super.updateContent();

        listener = new LocalFileItemAdapter.OnLocalFileItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                chosenFile = fileList.get(pos).file;
                File sel = new File(path + "/" + chosenFile);
                if (sel.isDirectory()) {
                    firstLvl = false;
                    // Adds chosen directory to list
                    str.add(chosenFile);
                    fileList = null;
                    path = new File(sel + "");

                    getFolderPath();
                    loadFileList();

                    recyclerView.setAdapter(adapter);

                    Log.d(TAG, path.getAbsolutePath());

                }
                // Checks if 'up' was clicked
                else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

                    // present directory removed from list
                    String s = "";
                    if (str.size() > 0) {
                        s = str.remove(str.size() - 1);
                    }
                    // path modified to exclude present directory
                    path = new File(path.toString().substring(0,
                            path.toString().lastIndexOf(s)));
                    fileList = null;

                    // if there are no more directories in the list, then
                    // its the first level
                    if (str.isEmpty()) {
                        firstLvl = true;
                    }
                    getFolderPath();
                    loadFileList();
                    recyclerView.setAdapter(adapter);
                    Log.d(TAG, path.getAbsolutePath());

                }
                // File picked
                else {
                    // Perform action with file picked

                    ((MainActivity) getActivity()).getAppViewModel().LoacalManga.setSelectedFilePath(path, chosenFile);
                    getActivity().startActivity(new Intent(getActivity(), LocalReadActivity.class));
                    getActivity().overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
                }
            }

        };

        adapter.setOnClickListener(listener);
        recyclerView.setAdapter(adapter);

    }

    private void getFolderPath() {
        extraPath = "";
        for (int i = 0; i < str.size(); i++) {
            extraPath = extraPath + "/" + str.get(i);
        }
    }

    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card ");
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return ((sel.isFile() && sel.getName().contains(".zip")) || sel.isDirectory())
                            && !sel.isHidden();

                }
            };

            String[] fList = path.list(filter);
            fileList = new ArrayList<>();
            if (fList != null) {
                for (int i = 0; i < fList.length; i++) {
                    fileList.add(new Item(fList[i], R.mipmap.ic_collections_black));

                    // Convert into file path
                    File sel = new File(path, fList[i]);

                    // Set drawables
                    if (sel.isDirectory()) {
                        fileList.get(i).icon = R.mipmap.ic_folder_open_black;
                        fileList.get(i).fileType = 0;
                        Log.d("DIRECTORY", fileList.get(i).file);
                    } else {
                        Log.d("FILE", fileList.get(i).file);
                    }
                }
            }
            Collections.sort(fileList);
            if (extraPath != "") {
                fileList.add(0, new Item("Up", R.mipmap.ic_publish_black));
            }
        } else {
            Log.e(TAG, "path does not exist");
        }

        if (fileList != null) {
            adapter = new LocalFileItemAdapter(getContext(), fileList, listener);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(extraPath);
            }
        });


    }

    public class Item implements Comparable<Item> {
        public String file;
        public int icon;
        public int fileType; // 0 folder, 1 file

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
            this.fileType = 1;
        }

        @Override
        public String toString() {
            return file;
        }

        @Override
        public int compareTo(Item another) {
            if (this.fileType < another.fileType) {
                return -1;
            } else {
                return this.file.compareTo(another.file);
            }
        }
    }

}