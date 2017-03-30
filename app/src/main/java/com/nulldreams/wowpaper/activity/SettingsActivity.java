package com.nulldreams.wowpaper.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MenuItem;

import com.nulldreams.wowpaper.BuildConfig;
import com.nulldreams.wowpaper.R;
import com.nulldreams.wowpaper.WowApp;
import com.nulldreams.wowpaper.manager.ApiManager;
import com.nulldreams.wowpaper.modules.CountResult;
import com.nulldreams.wowpaper.modules.glide.MyGlideModule;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatPreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener{

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);

        //setSupportActionBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        final PreferenceScreen screen = this.getPreferenceScreen();
        if (!BuildConfig.DEBUG) {
            screen.removePreference(findPreference(getString(R.string.pref_key_count)));
        } else {
            ApiManager.getInstance(this).getQueryCount(new Callback<CountResult>() {
                @Override
                public void onResponse(Call<CountResult> call, Response<CountResult> response) {
                    screen.findPreference(getString(R.string.pref_key_count)).setSummary(response.body().counts.toString());
                }

                @Override
                public void onFailure(Call<CountResult> call, Throwable t) {

                }
            });
        }
        refreshPaperCache(screen);
        refreshGlideCache(screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().setOnPreferenceChangeListener(this);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().setOnPreferenceChangeListener(null);
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_key_original_pic_cache))) {
            clearPaperCache();
            refreshPaperCache(preferenceScreen);
            return true;
        } else if (key.equals(getString(R.string.pref_key_glide_pic_cache))) {
            clearGlideCache();
            refreshGlideCache(preferenceScreen);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void refreshPaperCache(PreferenceScreen preferenceScreen) {
        File cacheDir = WowApp.getPaperCacheDir(this);
        File[] files = cacheDir.listFiles();
        long length = 0;
        int count = 0;
        if (files != null) {
            count = files.length;
            for (File file : files) {
                length += file.length();
            }
        }
        String fileSize = Formatter.formatFileSize(this, length);
        preferenceScreen.findPreference(getString(R.string.pref_key_original_pic_cache)).setSummary(
                getString(R.string.pref_summary_original_pic_cache, count, fileSize)
        );
    }

    private void clearPaperCache() {
        File cacheDir = WowApp.getPaperCacheDir(this);
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    private void refreshGlideCache(PreferenceScreen preferenceScreen) {
        File cacheDir = WowApp.getGlideCacheDir(this);
        File[] files = cacheDir.listFiles();
        long length = 0;
        int count = 0;
        if (files != null) {
            count = files.length;
            for (File file : files) {
                length += file.length();
            }
        }
        String fileSize = Formatter.formatFileSize(this, length);
        String totalSize = Formatter.formatFileSize(this, MyGlideModule.CACHE_SIZE);
        preferenceScreen.findPreference(getString(R.string.pref_key_glide_pic_cache)).setSummary(
                getString(R.string.pref_summary_glide_pic_cache, count, fileSize, totalSize)
        );
    }

    private void clearGlideCache() {
        File cacheDir = WowApp.getGlideCacheDir(this);
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(TAG, "onSharedPreferenceChanged key=" + key + " value=" + sharedPreferences.getBoolean(key, false));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.v(TAG, "onPreferenceChange newValue=" + newValue);
        return false;
    }
}
