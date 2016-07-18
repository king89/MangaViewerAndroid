package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.MangaPageActivity;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

public class MangaChapterItemAdapter extends RecyclerView.Adapter<MangaChapterItemAdapter.RecyclerViewHolders> {

    int MAX_TITLE_LENGTH = 20;
    protected Context context;
    protected LayoutInflater mInflater = null;
    protected MangaViewModel viewModel;
    protected List<MangaChapterItem> chapter;

    public MangaChapterItemAdapter(Context context, MangaViewModel viewModel,
                                   List<MangaChapterItem> chapter) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.context = context;
        this.chapter = chapter;

    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return chapter.size();
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manga_chapter_item, parent, false);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        String chapterTitle = chapter.get(position).getTitle();
        if (chapter.get(position).getMenu().getTitle().length() > MAX_TITLE_LENGTH) {
            chapterTitle = chapterTitle.replace(chapter.get(position).getMenu().getTitle(), context.getString(R.string.prefix_chapter_title));
            chapter.get(position).setTitle(chapterTitle);
        }
        holder.textView.setText(chapterTitle);

    }


    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView textView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            imageView = (MyImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        @Override
        public void onClick(View view) {
            int position = getPosition();
            // TODO Auto-generated method stub
            viewModel.setSelectedMangaChapterItem(chapter.get(position));
            context.startActivity(new Intent(context, MangaPageActivity.class));
            ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);

        }
    }
}
