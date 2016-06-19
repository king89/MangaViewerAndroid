package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.MangaChapterActivity;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.util.AsyncImageLoader.ImageCallback;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.util.MangaHelper;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MangaMenuItemAdapter extends RecyclerView.Adapter<MangaMenuItemAdapter.RecyclerViewHolders> {
    private Context context;
    private MangaViewModel viewModel;
    private List<? extends MangaMenuItem> menu;
    private HashMap<String,Object> mStateHash;
    private boolean isFavouriteMangaMenu;
    EndlessScrollListener endlessScrollListener;

    public MangaMenuItemAdapter(Context context, MangaViewModel viewModel,
                                List<? extends MangaMenuItem> menu) {
        this.viewModel = viewModel;
        this.context = context;
        this.menu = menu;

    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return menu.size();
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_manga_menu_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {

        holder.imageView.setImageURL(this.menu.get(position), true, context.getResources().getDrawable(R.color.black));
        String title = this.menu.get(position).getTitle();
        holder.textView.setText(title);

    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void setEndlessScrollListener(EndlessScrollListener endlessScrollListener) {
        this.endlessScrollListener = endlessScrollListener;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView, countTextView;
        public MyImageView imageView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            imageView = (MyImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            countTextView = (TextView) itemView.findViewById(R.id.countTextView);
        }

        @Override
        public void onClick(View view) {
            int menuPos = getPosition();
            viewModel.setSelectedMangaMenuItem(menu.get(menuPos));
            context.startActivity(new Intent(context, MangaChapterActivity.class));
            ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);

        }
    }

    public interface EndlessScrollListener {
        /**
         * Loads more data.
         *
         * @param position
         * @return true loads data actually, false otherwise.
         */
        public void onLoadMore(List<MangaMenuItem> menuList, HashMap<String, Object> state);
    }


}
