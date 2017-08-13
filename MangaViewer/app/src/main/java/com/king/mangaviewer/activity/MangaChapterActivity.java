package com.king.mangaviewer.activity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaChapterItemAdapter;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.grantland.widget.AutofitTextView;

public class MangaChapterActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView listView;

    @BindView(R.id.imageView)
    MyImageView imageView;

    @BindView(R.id.textView)
    AutofitTextView textView;

    @BindView(R.id.progressBar)
    ProgressBar progreeBar;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_manga_chapter);
        ButterKnife.bind(this);

        initFAB();

        textView.setText(this.getAppViewModel().Manga.getSelectedMangaMenuItem().getTitle());
        imageView.setImageURL(this.getAppViewModel().Manga.getSelectedMangaMenuItem(), true, getResources().getDrawable(R.color.black));

        progreeBar.setVisibility(View.VISIBLE);
        compositeDisposable.add(Flowable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                MangaViewModel mangaViewModel = MangaChapterActivity.this
                        .getAppViewModel().Manga;

                List<MangaChapterItem> mList = MangaChapterActivity.this
                        .getMangaHelper().getChapterList(
                                mangaViewModel.getSelectedMangaMenuItem());
                mangaViewModel.setMangaChapterList(mList);

                return 1;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        update(null);
                    }
                }));
    }

    private void initFAB() {
        if (getAppViewModel().Setting.checkIsFavourited(getAppViewModel().Manga.getSelectedMangaMenuItem())) {
            floatingActionButton.setImageResource(R.mipmap.ic_star_white);
        } else {
            floatingActionButton.setImageResource(R.mipmap.ic_star_border_white);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //try to add, if yes, set favourited, if not, remove it from favourite list
                int chapterCount = getMangaViewModel().getMangaChapterList() == null ? 0 : getMangaViewModel().getMangaChapterList().size();
                if (getAppViewModel().Setting.addFavouriteManga(getAppViewModel().Manga.getSelectedMangaMenuItem(),
                        chapterCount)) {
                    floatingActionButton.setImageResource(R.mipmap.ic_star_white);
                    Toast.makeText(MangaChapterActivity.this, getString(R.string.favourited), Toast.LENGTH_SHORT).show();
                } else {
                    getAppViewModel().Setting.removeFavouriteManga(getAppViewModel().Manga.getSelectedMangaMenuItem());
                    floatingActionButton.setImageResource(R.mipmap.ic_star_border_white);
                    Toast.makeText(MangaChapterActivity.this, getString(R.string.unfavourited), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void update(Message msg) {
        // TODO Auto-generated method stub
        progreeBar.setVisibility(View.GONE);

        MangaChapterItemAdapter adapter = new MangaChapterItemAdapter(this,
                this.getAppViewModel().Manga,
                this.getAppViewModel().Manga.getMangaChapterList());

        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
    }

    @Override
    protected boolean IsCanBack() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    protected String getActionBarTitle() {
        // TODO Auto-generated method stub
        return this.getAppViewModel().Manga.getSelectedMangaMenuItem().getTitle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chapter_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

}
