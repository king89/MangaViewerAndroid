package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.MangaPageActivity;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

public class MangaChapterItemAdapter extends BaseAdapter {

    int MAX_TITLE_LENGTH = 20;
    protected Context context;
    protected LayoutInflater mInflater = null;
    protected MangaViewModel viewModel;
    protected AsyncImageLoader asyncImageLoader = null;
    protected List<MangaChapterItem> chapter;

    public MangaChapterItemAdapter(Context context, MangaViewModel viewModel,
                                   List<MangaChapterItem> chapter) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.context = context;
        this.chapter = chapter;

        asyncImageLoader = AsyncImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return chapter.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return chapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater
                    .inflate(R.layout.list_manga_chapter_item, null);
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.imageView);
            holder.textView = (TextView) convertView
                    .findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String chapterTitle = chapter.get(position).getTitle();
        if (chapter.get(position).getMenu().getTitle().length() > MAX_TITLE_LENGTH)
        {
            chapterTitle = chapterTitle.replace(chapter.get(position).getMenu().getTitle(),context.getString(R.string.prefix_chapter_title));
            chapter.get(position).setTitle(chapterTitle);
        }
        holder.textView.setText(chapterTitle);
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                viewModel.setSelectedMangaChapterItem(chapter.get(position));
                context.startActivity(new Intent(context, MangaPageActivity.class));
                ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);

            }
        });
        return convertView;

    }

    class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }


}
