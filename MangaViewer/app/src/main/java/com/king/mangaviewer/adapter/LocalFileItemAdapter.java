package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.LocalFragment;
import com.king.mangaviewer.activity.MangaChapterActivity;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

/**
 * Created by KinG on 7/11/2016.
 */
public class LocalFileItemAdapter extends RecyclerView.Adapter<LocalFileItemAdapter.RecyclerViewHolders> {
    private Context context;
    private LayoutInflater mInflater = null;
    private List<LocalFragment.Item> dateList;
    private OnLocalFileItemClickListener onClickListener;

    public LocalFileItemAdapter(Context context, List<LocalFragment.Item> item, OnLocalFileItemClickListener onClickListener) {
        this.context = context;
        this.dateList = item;
        this.onClickListener = onClickListener;
    }

    public void setOnClickListener(OnLocalFileItemClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public LocalFileItemAdapter.RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_local_file_item, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(LocalFileItemAdapter.RecyclerViewHolders holder, int position) {
        holder.textView.setText(dateList.get(position).file);
        // put the image on the text view
        holder.imageView.setImageResource(dateList.get(position).icon);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder {

        public TextView textView;
        public ImageView imageView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(v, getAdapterPosition());
                    }
                }
            });
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }

    }

    public interface OnLocalFileItemClickListener {
        public void onClick(View view, int pos);
    }
}
