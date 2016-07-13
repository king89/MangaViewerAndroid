package com.king.mangaviewer.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.NavDrawerListAdapter;
import com.king.mangaviewer.model.NavDrawerItem;
import com.king.mangaviewer.model.MangaWebSource;
import com.king.mangaviewer.service.AutoNotifyUpdatedService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final long DELAYTIME = 5000;
    private static final String STATE_KEY_POSITION = "state_key_position";
    CharSequence mTitle;
    CharSequence mDrawerTitle;
    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPagerAdapter mViewPagerAdapter;
    private SearchView searchView;
    private BaseFragment fragment = null;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavigationView navigationView;
    private ViewPager mViewPager;
    private int mSelectedPosition;
    private int mTwoTapToExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDrawer(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Log.i("MainActivity", "OnNewIntent");
        if (intent.getBooleanExtra(AutoNotifyUpdatedService.AUTO_UPDATE_SERVICE, false)) {
            intent.putExtra(AutoNotifyUpdatedService.AUTO_UPDATE_SERVICE, false);
//            displayView(getResources().getInteger(R.integer.menu_favourite_pos));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.i("MainActivity", "onResume");
        if (searchView != null) {
            searchView.onActionViewCollapsed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_KEY_POSITION, mSelectedPosition);
        super.onSaveInstanceState(outState);
    }

    private void initDrawer(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_latest_manga:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_all_manga:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_favorite:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_history:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.menu_local:
                        mViewPager.setCurrentItem(4);
                        break;
                    case R.id.menu_all_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    default:
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = mDrawerTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
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

//        if (savedInstanceState == null) {
//            // on first time display view for first nav item
//            if (getIntent().getBooleanExtra(AutoNotifyUpdatedService.AUTO_UPDATE_SERVICE, false)) {
//                getIntent().putExtra(AutoNotifyUpdatedService.AUTO_UPDATE_SERVICE, false);
//                displayView(getResources().getInteger(R.integer.menu_favourite_pos));
//            } else {
//                displayView(0);
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        displayView(mSelectedPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
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
                View v = findViewById(R.id.menu_setting);
                displayMangaSource(v);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayMangaSource(View anchorView) {
        final PopupWindow popup = new PopupWindow(this);
        View layout = getLayoutInflater().inflate(R.layout.menu_main_setting, null);

        ListView lv = (ListView) layout.findViewById(R.id.listView);

        final List<MangaWebSource> mws = getAppViewModel().Setting.getMangaWebSources();
        int tSelectWebSourcePos = 0;
        List<String> source = new ArrayList<>();
        if (mws != null) {
            int i = 0;
            for (MangaWebSource m : mws) {
                source.add(m.getDisplayName());
                if (m.getId() == getAppViewModel().Setting.getSelectedWebSource(this).getId()) {
                    tSelectWebSourcePos = i;
                }
                i++;
            }
        }

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, source));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAppViewModel().Setting.setSelectedWebSource(mws.get(position), MainActivity.this);
                getAppViewModel().Manga.setMangaMenuList(null);
                int currPos = mViewPager.getCurrentItem();
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setCurrentItem(currPos);
//                ((BaseFragment)mViewPagerAdapter.getItem(mViewPager.getCurrentItem())).refresh();
                popup.dismiss();
            }
        });
        lv.setItemChecked(tSelectWebSourcePos, true);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.showAsDropDown(anchorView);
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
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mTwoTapToExit = 0;
    }

    private void setupViewPager(ViewPager viewPager) {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mViewPagerAdapter.addFragment(new HomeFragment(), getString(R.string.nav_latest_update));
        mViewPagerAdapter.addFragment(new AllMangaFragment(), getString(R.string.nav_all_manga));
        mViewPagerAdapter.addFragment(new FavouriteFragment(), getString(R.string.nav_favourite));
        mViewPagerAdapter.addFragment(new HistoryFragment(), getString(R.string.nav_history));
        mViewPagerAdapter.addFragment(new LocalFragment(), getString(R.string.nav_local));
        viewPager.setAdapter(mViewPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mTwoTapToExit < 1) {
            if (!mDrawerLayout.isDrawerOpen(navigationView)) {
                mDrawerLayout.openDrawer(navigationView);
            } else {
                mDrawerLayout.closeDrawer(navigationView);
            }
            mTwoTapToExit++;
            exitAppHandler.removeCallbacks(exitAppRunable);
            exitAppHandler.postDelayed(exitAppRunable, DELAYTIME);
            Toast.makeText(this, getString(R.string.msg_tap_two_to_exit), Toast.LENGTH_LONG).show();
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

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
