package com.king.mangaviewer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.ui.main.fragment.LocalFragment;

import java.util.List;

/**
 * Created by KinG on 7/11/2016.
 */
public class LocalFileItemAdapter extends RecyclerView.Adapter<LocalFileItemAdapter.RecyclerViewHolders> {
    private Context context;
    private LayoutInflater mInflater = null;
    private List<LocalFragment.Item> list;
    private OnLocalFileItemClickListener onClickListener;

    public LocalFileItemAdapter(Context context, List<LocalFragment.Item> item, OnLocalFileItemClickListener onClickListener) {
        this.context = context;
        this.list = item;
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
        holder.textView.setText(list.get(position).file);
        // put the image on the text view
        holder.imageView.setImageResource(list.get(position).icon);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
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
