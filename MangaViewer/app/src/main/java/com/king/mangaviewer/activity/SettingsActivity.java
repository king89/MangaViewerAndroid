package com.king.mangaviewer.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.preference.MangaViewerDialogPreference;
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import static android.widget.Toast.*;


/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    private AppCompatDelegate mDelegate;
    private static final String LOG_TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();

    }

    public SettingViewModel getSettingViewModel() {
        return ((MyApplication) getApplication()).AppViewModel.Setting;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishSetting();
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void finishSetting() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        this.overridePendingTransition(R.anim.in_leftright, R.anim.out_leftright);
    }

    @Override
    public void onBackPressed() {
        finishSetting();
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        SettingViewModel mSettingViewMdoel = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());

            addPreferencesFromResource(R.xml.pref_general);
            mSettingViewMdoel = ((SettingsActivity) getActivity()).getSettingViewModel();
            //Cache Size
            final MangaViewerDialogPreference p = (MangaViewerDialogPreference) findPreference(getString(R.string.pref_key_setting_storage_size));
            p.setOnDialogClickListener(new MangaViewerDialogPreference.OnDialogClickListener() {
                @Override
                public void onClick() {
                    mSettingViewMdoel.resetMangaFolder(getActivity());
                    p.setSummary(mSettingViewMdoel.getMangaFolderSize(getActivity()));
                    makeText(getActivity(), getString(R.string.setting_msg_cache_cleared), LENGTH_SHORT).show();
                }
            });
            p.setSummary(mSettingViewMdoel.getMangaFolderSize(getActivity()));

            //Manga Sources
            final ListPreference mangaSourcesPref = (ListPreference) findPreference(getString(R.string.pref_key_manga_sources));
            String value = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(mangaSourcesPref.getKey(), "");
            CharSequence[] csEntries = new CharSequence[mSettingViewMdoel.getMangaWebSources().size()];
            CharSequence[] csValues = new CharSequence[mSettingViewMdoel.getMangaWebSources().size()];
            for (int i = 0; i < mSettingViewMdoel.getMangaWebSources().size(); i++) {
                csEntries[i] = mSettingViewMdoel.getMangaWebSources().get(i).getDisplayName();
                csValues[i] = "" + mSettingViewMdoel.getMangaWebSources().get(i).getId();
            }
            mangaSourcesPref.setEntries(csEntries);
            mangaSourcesPref.setEntryValues(csValues);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_manga_sources)));




            //auto update hour
            final ListPreference autoUpdateHour = (ListPreference)findPreference(getString(R.string.pref_key_auto_update_hours));
            int index = autoUpdateHour.findIndexOfValue(sp.getString(getString(R.string.pref_key_auto_update_hours),"6"));
            autoUpdateHour.setSummary(autoUpdateHour.getEntries()[index]);
            autoUpdateHour.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AutoUpdateAlarmReceiver receiver = new AutoUpdateAlarmReceiver();
                    receiver.cancelAlarm(getActivity());
                    receiver.setAlarm(getActivity());
                    int index = autoUpdateHour.findIndexOfValue(newValue.toString());
                    autoUpdateHour.setSummary(autoUpdateHour.getEntries()[index]);
                    return true;
                }
            });
            //auto update service
            final SwitchPreference autoUpdateServicePref = (SwitchPreference) findPreference(getString(R.string.pref_key_auto_update_service));
            autoUpdateServicePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AutoUpdateAlarmReceiver receiver = new AutoUpdateAlarmReceiver();
                    if ((boolean) newValue) {
                        receiver.setAlarm(getActivity());
                        Toast.makeText(getActivity(), getString(R.string.msg_start_auto_update_service), Toast.LENGTH_SHORT).show();
                        autoUpdateHour.setEnabled(true);
                    } else {
                        receiver.cancelAlarm(getActivity());
                        Toast.makeText(getActivity(), getString(R.string.msg_stop_auto_update_service), Toast.LENGTH_SHORT).show();
                        autoUpdateHour.setEnabled(false);
                    }
                    return true;
                }
            });

            //set auto update hour enable
            boolean isEnable = sp.getBoolean(getString(R.string.pref_key_auto_update_service),true);
            autoUpdateHour.setEnabled(isEnable);
        }
    }


}
