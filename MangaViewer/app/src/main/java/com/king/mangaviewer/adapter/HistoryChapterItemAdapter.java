package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.BaseActivity;
import com.king.mangaviewer.activity.MangaPageActivity;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.model.HistoryMangaChapterItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

public class HistoryChapterItemAdapter extends BaseAdapter {

    protected Context context;
    protected LayoutInflater mInflater = null;
    protected MangaViewModel viewModel;
    protected AsyncImageLoader asyncImageLoader = null;
    protected List<HistoryMangaChapterItem> list;

    public HistoryChapterItemAdapter(Context context, MangaViewModel viewModel,
                                     List<HistoryMangaChapterItem> list) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.context = context;
        this.list = list;

        asyncImageLoader = AsyncImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
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

            convertView = mInflater.inflate(R.layout.list_history_chapter_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.chapterTextView = (TextView) convertView.findViewById(R.id.chapterTextView);
            holder.dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
            holder.sourceTextView = (TextView) convertView.findViewById(R.id.sourceTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drawable cachedImage = asyncImageLoader.loadImageFromMenuItem(context, list.get(position).getMenu(),
                holder.imageView, new AsyncImageLoader.ImageCallback() {

                    public void imageLoaded(Drawable imageDrawable,
                                            ImageView imageView, String imageUrl) {
                        // TODO Auto-generated method stub
                        if (imageDrawable != null) {
                            imageView.setImageDrawable(imageDrawable);
                        }
                    }
                });
        if (cachedImage != null) {
            holder.imageView.setImageDrawable(cachedImage);
        }

        holder.titleTextView.setText(list.get(position).getMenu().getTitle());
        holder.chapterTextView.setText(list.get(position).getTitle());
        holder.dateTextView.setText(list.get(position).getLastReadDate());
        holder.sourceTextView.setText(list.get(position).getMangaWebSource().getDisplayName());

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //use a new thread to load chapter list, this has to
                //TODO change to use thread pool
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.setMangaChapterList(((BaseActivity)context).getMangaHelper().getChapterList(list.get(position).getMenu()));
                    }
                }).start();

                viewModel.setSelectedMangaChapterItem(list.get(position));
                context.startActivity(new Intent(context, MangaPageActivity.class));
                ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
            }
        });
        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView titleTextView;
        public TextView chapterTextView;
        public TextView dateTextView;
        public TextView sourceTextView;
    }


}