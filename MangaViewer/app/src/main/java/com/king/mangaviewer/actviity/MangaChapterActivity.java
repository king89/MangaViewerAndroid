package com.king.mangaviewer.actviity;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.adapter.MangaChapterItemAdapter;
import com.king.mangaviewer.common.AsyncImageLoader;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

public class MangaChapterActivity extends BaseActivity {

    protected ProgressDialog progressDialog;

    ListView listView = null;
    ImageView imageView = null;
    TextView textView = null;

    @Override
    protected void initControl() {
        // TODO Auto-generated method stub
        setContentView(R.layout.activity_manga_chapter);

        listView = (ListView) this.findViewById(R.id.listView);
        imageView = (ImageView) this.findViewById(R.id.imageView);
        textView = (TextView) this.findViewById(R.id.textView);

        textView.setText(this.getAppViewModel().Manga.getSelectedMangaMenuItem().getTitle());
        String imagePath = this.getAppViewModel().Manga.getSelectedMangaMenuItem().getImagePath();
        Drawable cachedImage = new AsyncImageLoader().loadDrawable(imagePath,
                imageView, new AsyncImageLoader.ImageCallback() {

                    public void imageLoaded(Drawable imageDrawable,
                                            ImageView imageView, String imageUrl) {
                        // TODO Auto-generated method stub
                        if (imageDrawable != null) {
                            imageView.setImageDrawable(imageDrawable);
                        }


                    }
                });
        if (cachedImage != null) {
            imageView.setImageDrawable(cachedImage);
        }
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

    @Override
    protected void update(Message msg) {
        // TODO Auto-generated method stub
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        ListAdapter adapter = new MangaChapterItemAdapter(this,
                this.getAppViewModel().Manga,
                this.getAppViewModel().Manga.getMangaChapterList());

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

        if (getAppViewModel().Setting.checkIsFavourited(getAppViewModel().Manga.getSelectedMangaMenuItem())) {
            menu.getItem(0).setIcon(R.mipmap.ic_star_white);
        }
        else {
            menu.getItem(0).setIcon(R.mipmap.ic_star_border_white);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_favourite)
        {
            //try to add, if yes, set favourited, if not, remove it from favourite list
            if(getAppViewModel().Setting.addFavouriteManga(getAppViewModel().Manga.getSelectedMangaMenuItem()))
            {
                item.setIcon(R.mipmap.ic_star_white);
                Toast.makeText(this,getString(R.string.favourited),Toast.LENGTH_SHORT).show();
            }else {
                getAppViewModel().Setting.removeFavouriteManga(getAppViewModel().Manga.getSelectedMangaMenuItem());
                item.setIcon(R.mipmap.ic_star_border_white);
                Toast.makeText(this,getString(R.string.unfavourited),Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
