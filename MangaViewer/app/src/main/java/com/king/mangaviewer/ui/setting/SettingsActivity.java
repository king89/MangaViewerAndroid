package com.king.mangaviewer.ui.setting;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import com.bumptech.glide.Glide;
import com.king.mangaviewer.MyApplication;
import com.king.mangaviewer.R;
import com.king.mangaviewer.base.BaseActivity;
import com.king.mangaviewer.preference.MangaViewerDialogPreference;
import com.king.mangaviewer.service.AutoUpdateAlarmReceiver;
import com.king.mangaviewer.util.Util;
import com.king.mangaviewer.viewmodel.SettingViewModel;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

/**
 * A {@link SettingsActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends BaseActivity {

    private static final String LOG_TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                new GeneralPreferenceFragment()).commit();

    }

    @Override
    protected void initControl() {
        setContentView(R.layout.activity_settings);
    }

    public SettingViewModel getSettingViewModel() {
        return ((MyApplication) getApplication()).getAppViewModel().Setting;
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
    public static class GeneralPreferenceFragment extends PreferenceFragmentCompat {
        SettingViewModel mSettingViewModel = null;

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            final Context ctx = getPreferenceManager().getContext();

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);

            addPreferencesFromResource(R.xml.pref_general);
            mSettingViewModel = ((SettingsActivity) getActivity()).getSettingViewModel();
            //Cache Size
            final MangaViewerDialogPreference p = (MangaViewerDialogPreference) findPreference(
                    getString(R.string.pref_key_setting_storage_size));
            p.setOnDialogClickListener(new MangaViewerDialogPreference.OnDialogClickListener() {
                @Override
                public void onClick() {
                    Observable.fromCallable(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            mSettingViewModel.resetMangaFolder(ctx);
                            Glide.get(ctx).clearDiskCache();
                            return 1;
                        }
                    }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Object>() {
                                @Override
                                public void accept(Object o) throws Exception {
                                    Glide.get(ctx).clearMemory();
                                    p.setSummary(mSettingViewModel.getCacheFolderSize(ctx));
                                    makeText(ctx, getString(R.string.setting_msg_cache_cleared),
                                            LENGTH_SHORT).show();
                                }
                            });


                }
            });
            p.setSummary(mSettingViewModel.getCacheFolderSize(ctx));

            //Manga Sources
            final ListPreference mangaSourcesPref = (ListPreference) findPreference(
                    getString(R.string.pref_key_manga_sources));
            String value = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                    mangaSourcesPref.getKey(), "");
            CharSequence[] csEntries = new CharSequence[mSettingViewModel.getMangaWebSources().size()];
            CharSequence[] csValues = new CharSequence[mSettingViewModel.getMangaWebSources().size()];
            for (int i = 0; i < mSettingViewModel.getMangaWebSources().size(); i++) {
                csEntries[i] = mSettingViewModel.getMangaWebSources().get(i).getDisplayName();
                csValues[i] = "" + mSettingViewModel.getMangaWebSources().get(i).getId();
            }
            mangaSourcesPref.setEntries(csEntries);
            mangaSourcesPref.setEntryValues(csValues);
            bindPreferenceSummaryToValue(
                    findPreference(getString(R.string.pref_key_manga_sources)));

            //auto update hour
            final ListPreference autoUpdateHour = (ListPreference) findPreference(
                    getString(R.string.pref_key_auto_update_hours));
            int index = autoUpdateHour.findIndexOfValue(
                    sp.getString(getString(R.string.pref_key_auto_update_hours), "6"));
            autoUpdateHour.setSummary(autoUpdateHour.getEntries()[index]);
            autoUpdateHour.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            AutoUpdateAlarmReceiver receiver = new AutoUpdateAlarmReceiver();
                            receiver.cancelAlarm(ctx);
                            receiver.setAlarm(ctx);
                            int index = autoUpdateHour.findIndexOfValue(newValue.toString());
                            autoUpdateHour.setSummary(autoUpdateHour.getEntries()[index]);
                            return true;
                        }
                    });
            //auto update service
            final SwitchPreferenceCompat autoUpdateServicePref = (SwitchPreferenceCompat) findPreference(
                    getString(R.string.pref_key_auto_update_service));
            autoUpdateServicePref.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener() {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue) {
                            AutoUpdateAlarmReceiver receiver = new AutoUpdateAlarmReceiver();
                            if ((boolean) newValue) {
                                receiver.setAlarm(ctx);
                                Toast.makeText(ctx,
                                        getString(R.string.msg_start_auto_update_service),
                                        Toast.LENGTH_SHORT).show();
                                autoUpdateHour.setEnabled(true);
                            } else {
                                receiver.cancelAlarm(ctx);
                                Toast.makeText(ctx,
                                        getString(R.string.msg_stop_auto_update_service),
                                        Toast.LENGTH_SHORT).show();
                                autoUpdateHour.setEnabled(false);
                            }
                            return true;
                        }
                    });

            final Preference versionPref = findPreference(
                    getString(R.string.pref_key_version_name));
            versionPref.setSummary(Util.getVersionName(ctx));

            //set auto update hour enable
            boolean isEnable = sp.getBoolean(getString(R.string.pref_key_auto_update_service),
                    true);
            autoUpdateHour.setEnabled(isEnable);
        }
    }

}
