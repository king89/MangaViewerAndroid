package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.king.mangaviewer.R;
import com.king.mangaviewer.ui.page.MangaPageActivityV2;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.model.HistoryMangaChapterItem;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.viewmodel.MangaViewModel;
import java.util.List;

import static com.king.mangaviewer.activity.MangaPageActivity.INTENT_EXTRA_FROM_HISTORY;

public class HistoryChapterItemAdapter extends RecyclerView.Adapter<HistoryChapterItemAdapter.RecyclerViewHolders> {

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
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history_chapter_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.imageView.setImageURL(list.get(position).getMenu(), true, context.getResources().getDrawable(R.color.black));
        holder.titleTextView.setText(list.get(position).getMenu().getTitle());
        holder.chapterTextView.setText(list.get(position).getTitle());
        holder.dateTextView.setText(list.get(position).getLastReadDate());
        holder.sourceTextView.setText(list.get(position).getMangaWebSource().getDisplayName());
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public MyImageView imageView;
        public TextView titleTextView;
        public TextView chapterTextView;
        public TextView dateTextView;
        public TextView sourceTextView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            imageView = (MyImageView) itemView.findViewById(R.id.imageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            chapterTextView = (TextView) itemView.findViewById(R.id.chapterTextView);
            dateTextView = (TextView) itemView.findViewById(R.id.dateTextView);
            sourceTextView = (TextView) itemView.findViewById(R.id.sourceTextView);
        }

        @Override
        public void onClick(View view) {
            final int menuPos = getPosition();

            viewModel.setSelectedMangaChapterItem(list.get(menuPos));
            Intent intent = new Intent(context, MangaPageActivityV2.class);
            intent.putExtra(INTENT_EXTRA_FROM_HISTORY, true);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);

        }
    }
}
