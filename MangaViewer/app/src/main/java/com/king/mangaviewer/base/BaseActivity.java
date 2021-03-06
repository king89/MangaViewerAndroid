package com.king.mangaviewer.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import com.king.mangaviewer.R;
import com.king.mangaviewer.viewmodel.AppViewModel;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import com.king.mangaviewer.viewmodel.SettingViewModel;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.disposables.CompositeDisposable;
import javax.inject.Inject;

public class BaseActivity extends DaggerAppCompatActivity {

    private static final String KEY_MANGA_VIEW_MODEL = "key_manga_view_model";
    private static final String TAG = "BaseActivity";
    public Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            update(msg);
        }
    };
    public CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected Toolbar mToolbar;

    @Inject
    protected AppViewModel mAppViewModel;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initControl();
        initActionBar();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putString(KEY_MANGA_VIEW_MODEL, GsonHelper.INSTANCE.toJson(getMangaViewModel()));
        super.onSaveInstanceState(outState);
    }

    protected void initActionBar() {
        // TODO Auto-generated method stub
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        //final View toolbarContainerView = findViewById(R.id.toolbar_container);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(IsCanBack());
            actionBar.setTitle(getActionBarTitle());
        }
    }

    protected void initControl() {
        // TODO Auto-generated method stub

    }

    protected String getActionBarTitle() {
        return (String) this.getTitle();

    }

    protected void setActionBarTitle(String title) {
        this.getSupportActionBar().setTitle(title);
    }

    protected boolean IsCanBack() {
        return true;
    }

    protected void update(Message msg) {

    }

    @Override
    public void onBackPressed() {
        if (IsCanBack()) {
            goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSettingViewModel().saveSetting(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            goBack();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void goBack() {
        finish();
        overridePendingTransition(R.anim.in_leftright, R.anim.out_leftright);
    }

    public AppViewModel getAppViewModel() {
        return mAppViewModel;
    }

    public SettingViewModel getSettingViewModel() {
        return getAppViewModel().Setting;
    }

    public MangaViewModel getMangaViewModel() {
        return getAppViewModel().Manga;
    }

    public void showLoading() {
    }

    public void hideLoading() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
