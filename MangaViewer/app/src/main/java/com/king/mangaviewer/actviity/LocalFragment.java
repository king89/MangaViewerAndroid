package com.king.mangaviewer.actviity;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.king.mangaviewer.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class LocalFragment extends Fragment {

    private static final String TAG = "F_PATH";
    // Stores names of traversed directories
    ArrayList<String> str = new ArrayList<String>();
    ListAdapter adapter;
    // Check if the first level of the directory structure is the one showing
    private Boolean firstLvl = true;
    private ListView lv;
    private Item[] fileList;
    private File path;
    private String chosenFile;


    public LocalFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String defalutPaht = ((MainActivity) this.getActivity()).getAppViewModel().Setting.getDefaultLocalMangaPath();
        //TODO
        path = new File(Environment.getExternalStorageDirectory() + defalutPaht);
        View rootView = inflater.inflate(R.layout.fragment_local, container, false);

        lv = (ListView) rootView.findViewById(R.id.listView);
        loadFileList();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chosenFile = fileList[position].file;
                File sel = new File(path + "/" + chosenFile);
                if (sel.isDirectory()) {
                    firstLvl = false;

                    // Adds chosen directory to list
                    str.add(chosenFile);
                    fileList = null;
                    path = new File(sel + "");

                    loadFileList();

                    lv.setAdapter(adapter);
                    Log.d(TAG, path.getAbsolutePath());

                }

                // Checks if 'up' was clicked
                else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

                    // present directory removed from list
                    String s = str.remove(str.size() - 1);

                    // path modified to exclude present directory
                    path = new File(path.toString().substring(0,
                            path.toString().lastIndexOf(s)));
                    fileList = null;

                    // if there are no more directories in the list, then
                    // its the first level
                    if (str.isEmpty()) {
                        firstLvl = true;
                    }

                    loadFileList();
                    lv.setAdapter(adapter);
                    Log.d(TAG, path.getAbsolutePath());

                }
                // File picked
                else {
                    // Perform action with file picked

                    ((MainActivity) getActivity()).getAppViewModel().LoacalManga.setSelectedFilePath(path.getAbsolutePath() + "/" + chosenFile);
                    getActivity().startActivity(new Intent(getActivity(), LocalReadActivity.class));
                    getActivity().overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
                }
            }
        });
        //showDialog(DIALOG_LOAD_FILE);
        Log.d(TAG, path.getAbsolutePath());

        return rootView;
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
                    return (sel.isFile() || sel.isDirectory())
                            && !sel.isHidden();

                }
            };

            String[] fList = path.list(filter);
            fileList = new Item[fList.length];
            for (int i = 0; i < fList.length; i++) {
                fileList[i] = new Item(fList[i], R.drawable.ic_launcher);

                // Convert into file path
                File sel = new File(path, fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    fileList[i].icon = R.drawable.ic_launcher;
                    Log.d("DIRECTORY", fileList[i].file);
                } else {
                    Log.d("FILE", fileList[i].file);
                }
            }

            if (!firstLvl) {
                Item temp[] = new Item[fileList.length + 1];
                for (int i = 0; i < fileList.length; i++) {
                    temp[i + 1] = fileList[i];
                }
                temp[0] = new Item("Up", R.drawable.ic_launcher);
                fileList = temp;
            }
        } else {
            Log.e(TAG, "path does not exist");
        }

        if (fileList != null) {
            adapter = new ArrayAdapter<Item>(this.getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1,
                    fileList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // creates view
                    View view = super.getView(position, convertView, parent);
                    TextView textView = (TextView) view
                            .findViewById(android.R.id.text1);

                    // put the image on the text view
                    textView.setCompoundDrawablesWithIntrinsicBounds(
                            fileList[position].icon, 0, 0, 0);

                    // add margin between image and text (support various screen
                    // densities)
                    int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                    textView.setCompoundDrawablePadding(dp5);

                    return view;
                }
            };
        }

    }

    private class Item {
        public String file;
        public int icon;

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }

}