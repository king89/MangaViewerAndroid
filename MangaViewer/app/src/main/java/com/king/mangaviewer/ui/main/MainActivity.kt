package com.king.mangaviewer.ui.main

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.design.widget.TabLayout.OnTabSelectedListener
import android.support.design.widget.TabLayout.Tab
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import com.king.mangaviewer.R
import com.king.mangaviewer.base.BaseActivity
import com.king.mangaviewer.base.ViewModelFactory
import com.king.mangaviewer.di.annotation.ActivityScopedFactory
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver
import com.king.mangaviewer.ui.main.fragment.AllMangaFragment
import com.king.mangaviewer.ui.main.fragment.FavouriteFragment
import com.king.mangaviewer.ui.main.fragment.HistoryFragment
import com.king.mangaviewer.ui.main.fragment.HomeFragment
import com.king.mangaviewer.ui.main.fragment.LocalFragment
import com.king.mangaviewer.ui.search.SearchResultActivity
import com.king.mangaviewer.ui.setting.SettingsActivity
import com.king.mangaviewer.util.AppNavigator
import kotlinx.android.synthetic.main.activity_main_menu.fab
import kotlinx.android.synthetic.main.activity_main_menu.tabLayout
import java.util.ArrayList
import javax.inject.Inject

class MainActivity : BaseActivity() {
    private val FAVOURITE_POS = 0
    private val ALL_POS = 1

    internal var mTitle: CharSequence = ""
    internal var mDrawerTitle: CharSequence = ""
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mTabLayout: TabLayout
    lateinit var mDrawerToggle: ActionBarDrawerToggle
    lateinit var mViewPagerAdapter: ViewPagerAdapter
    private var searchView: SearchView? = null
    private var fragment: Fragment? = null
    // slide menu items
    lateinit var navigationView: NavigationView
    lateinit var mViewPager: ViewPager
    private val mSelectedPosition: Int = 0
    private var mTwoTapToExit: Int = 0

    @Inject
    @field:ActivityScopedFactory
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var appNavigator: AppNavigator

    private val exitAppHandler = Handler()
    private val exitAppRunable = Runnable { mTwoTapToExit = 0 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer(savedInstanceState)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //Log.i("MainActivity", "OnNewIntent");
        if (intent.getBooleanExtra(AutoUpdateAlarmReceiver.AUTO_UPDATE_SERVICE,
                        false)) {
            intent.putExtra(AutoUpdateAlarmReceiver.AUTO_UPDATE_SERVICE, false)
            //            displayView(getResources().getInteger(R.integer.menu_favourite_pos));
            mViewPager.currentItem = FAVOURITE_POS
        }
    }

    override fun IsCanBack(): Boolean {
        return false
    }

    override fun onResume() {
        super.onResume()
        //Log.i("MainActivity", "onResume");
        searchView?.onActionViewCollapsed()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_KEY_POSITION, mSelectedPosition)
        super.onSaveInstanceState(outState)
    }

    private fun initDrawer(savedInstanceState: Bundle?) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navigationView = findViewById<View>(R.id.navigation_view) as NavigationView
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_latest_manga -> mViewPager.currentItem = 0
                R.id.menu_all_manga -> mViewPager.currentItem = 1
                R.id.menu_favorite -> mViewPager.currentItem = 2
                R.id.menu_history -> mViewPager.currentItem = 3
                R.id.menu_local -> mViewPager.currentItem = 4
                R.id.menu_all_settings -> startActivity(
                        Intent(this@MainActivity, SettingsActivity::class.java))
                else -> {
                }
            }

            mDrawerLayout.closeDrawers()
            true
        }

        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        mDrawerTitle = title
        mTitle = mDrawerTitle
        mDrawerToggle = object : ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            override fun onDrawerClosed(view: View) {
                supportActionBar!!.setTitle(mTitle)
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View) {
                supportActionBar!!.setTitle(mDrawerTitle)
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu()
            }
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle)

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            if (intent.getBooleanExtra(
                            AutoUpdateAlarmReceiver.AUTO_UPDATE_SERVICE, false)) {
                intent.putExtra(AutoUpdateAlarmReceiver.AUTO_UPDATE_SERVICE,
                        false)
                mViewPager.currentItem = ALL_POS
            } else {
                //
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView?.setSearchableInfo(
                searchManager.getSearchableInfo(
                        ComponentName(this, SearchResultActivity::class.java)))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            R.id.menu_setting -> {
                val v = findViewById<View>(R.id.menu_setting)
                displayMangaSource(v)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayMangaSource(anchorView: View) {
        val popup = PopupWindow(this)
        val layout = layoutInflater.inflate(R.layout.menu_main_setting, null)

        val lv = layout.findViewById<View>(R.id.listView) as ListView

        val mws = appViewModel.Setting.mangaWebSources
        var tSelectWebSourcePos = 0
        val source = ArrayList<String>()
        if (mws != null) {
            for ((i, m) in mws.withIndex()) {
                source.add(m.displayName)
                if (m.id == appViewModel.Setting.selectedWebSource.id) {
                    tSelectWebSourcePos = i
                }
            }
        }

        lv.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice,
                source)
        lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            appViewModel.Setting.setSelectedWebSource(mws!![position],
                    this@MainActivity)
            appViewModel.Manga.mangaMenuList = null
            val currPos = mViewPager.currentItem
            mViewPager.adapter = mViewPagerAdapter
            mViewPager.currentItem = currPos
            //                ((BaseFragment)mViewPagerAdapter.getItem(mViewPager.getCurrentItem())).refresh();
            popup.dismiss()
        }
        lv.setItemChecked(tSelectWebSourcePos, true)
        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        // Show anchored to button
        popup.showAsDropDown(anchorView)
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig)
    }

    override fun initControl() {
        setContentView(R.layout.activity_main_menu)
        mTabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        mViewPager = findViewById<View>(R.id.viewPager) as ViewPager
        setupViewPager(mViewPager)
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabReselected(tab: Tab?) {
                val pos = mViewPager.currentItem
                fragment = mViewPagerAdapter.getItem(pos)
                (fragment as? HasFloatActionButton)?.initFab(fab)
            }

            override fun onTabUnselected(tab: Tab?) {
            }

            override fun onTabSelected(tab: Tab?) {
                val pos = mViewPager.currentItem
                fragment = mViewPagerAdapter.getItem(pos)
                if (fragment is HasFloatActionButton) {
                    (fragment as HasFloatActionButton).initFab(fab)
                } else {
                    fab.hide()
                }
            }

        })

        fab.setOnClickListener {
            (fragment as? HasFloatActionButton)?.onClick()
        }

        mTabLayout.setupWithViewPager(mViewPager)
        mTwoTapToExit = 0
    }

    private fun setupViewPager(viewPager: ViewPager) {
        mViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        mViewPagerAdapter.addFragment(FavouriteFragment(), getString(R.string.nav_favourite))
        mViewPagerAdapter.addFragment(HomeFragment(), getString(R.string.nav_latest_update))
        mViewPagerAdapter.addFragment(AllMangaFragment(), getString(R.string.nav_all_manga))
        mViewPagerAdapter.addFragment(HistoryFragment(), getString(R.string.nav_history))
        mViewPagerAdapter.addFragment(LocalFragment(), getString(R.string.nav_local))

        viewPager.adapter = mViewPagerAdapter
        viewPager.currentItem = ALL_POS
    }

    override fun onBackPressed() {
        if (mTwoTapToExit < 1) {
            if (!mDrawerLayout.isDrawerOpen(navigationView)) {
                mDrawerLayout.openDrawer(navigationView)
            } else {
                mDrawerLayout.closeDrawer(navigationView)
            }
            mTwoTapToExit++
            exitAppHandler.removeCallbacks(exitAppRunable)
            exitAppHandler.postDelayed(exitAppRunable, DELAYTIME)
            Toast.makeText(this, getString(R.string.msg_tap_two_to_exit), Toast.LENGTH_LONG).show()
        } else {
            super.onBackPressed()
        }

    }

    class ViewPagerAdapter(manager: FragmentManager) :
            FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

    }

    companion object {

        private val DELAYTIME: Long = 5000
        private val STATE_KEY_POSITION = "state_key_position"
    }
}
