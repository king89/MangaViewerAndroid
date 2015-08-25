package com.king.mangaviewer.actviity;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.NavDrawerListAdapter;
import com.king.mangaviewer.common.util.NavDrawerItem;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private static final long DELAYTIME = 5000;
    CharSequence mTitle;
    CharSequence mDrawerTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private int mSelectedPosition;
    private int mTwoTapToExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDrawer(savedInstanceState);

    }

    private void initDrawer(Bundle savedInstanceState) {
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_drawer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        for (int i = 0; i < navMenuTitles.length; i++) {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayView(position);
            }
        });
        // enabling action bar app icon and behaving it as toggle buttongetSupportActionBar()().setDisplayHomeAsUpEnabled(true);getSupportActionBar()().setHomeButtonEnabled(true);


        mTitle = mDrawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.mipmap.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);

        }
    }

    @Override
    public void update(Message msg) {
        // TODO Auto-generated method stub

    }


    private void displayView(int position) {
        // update the main content by replacing fragments

        BaseFragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new AllMangaFragment();
                break;
            case 2:
                fragment = new LocalFragment();
                break;
            case 3:
                fragment = new FavouriteFragment();
                break;
            case 4:
                mDrawerList.setItemChecked(mSelectedPosition, true);
                mDrawerList.setSelection(mSelectedPosition);
                mTitle = navMenuTitles[mSelectedPosition];
                this.setActionBarTitle(navMenuTitles[mSelectedPosition]);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivityForResult(new Intent(this, SettingsActivity.class), 1);
                this.overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
                break;
            default:
                break;
        }

        if (fragment != null) {

            //set fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mSelectedPosition = position;
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mTitle = navMenuTitles[position];
            this.setActionBarTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        displayView(mSelectedPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.menu_setting:
                new AlertDialog.Builder(this).setMessage("Setting").show();
                return true;
            case R.id.menu_refresh:
                displayView(mDrawerList.getCheckedItemPosition());
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_main_menu);
        mTwoTapToExit = 0;
    }

    @Override
    public void onBackPressed() {
        if (mTwoTapToExit < 1) {
            if (!mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.openDrawer(mDrawerList);
            }else {
                mDrawerLayout.closeDrawer(mDrawerList);
            }
            mTwoTapToExit++;
            exitAppHandler.removeCallbacks(exitAppRunable);
            exitAppHandler.postDelayed(exitAppRunable, DELAYTIME);
            Toast.makeText(this,getString(R.string.msg_tap_two_to_exit),Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }

    }

    private Handler exitAppHandler = new Handler();
    private Runnable exitAppRunable = new Runnable() {
        @Override
        public void run() {
            mTwoTapToExit = 0;
        }
    };
}
