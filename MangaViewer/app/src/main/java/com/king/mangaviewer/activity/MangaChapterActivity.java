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
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaChapterItemAdapter;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

import me.grantland.widget.AutofitTextView;

public class MangaChapterActivity extends BaseActivity {

    protected ProgressDialog progressDialog;

    RecyclerView listView = null;
    MyImageView imageView = null;
    AutofitTextView textView = null;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_manga_chapter);

        listView = (RecyclerView) this.findViewById(R.id.recyclerView);
        imageView = (MyImageView) this.findViewById(R.id.imageView);
        textView = (AutofitTextView) this.findViewById(R.id.textView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        initFAB();


        textView.setText(this.getAppViewModel().Manga.getSelectedMangaMenuItem().getTitle());
        imageView.setImageURL(this.getAppViewModel().Manga.getSelectedMangaMenuItem(), true, getResources().getDrawable(R.color.black));
        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                MangaViewModel mangaViewModel = MangaChapterActivity.this
                        .getAppViewModel().Manga;

                List<MangaChapterItem> mList = MangaChapterActivity.this
                        .getMangaHelper().getChapterList(
                                mangaViewModel.getSelectedMangaMenuItem());
                mangaViewModel.setMangaChapterList(mList);

                handler.sendEmptyMessage(0);

            }
        }.start();
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
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

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
