package com.king.mangaviewer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.king.mangaviewer.R;
import com.king.mangaviewer.activity.MangaChapterActivity;
import com.king.mangaviewer.component.MyImageView;
import com.king.mangaviewer.datasource.FavouriteMangaDataSource;
import com.king.mangaviewer.model.FavouriteMangaMenuItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.util.AsyncImageLoader;
import com.king.mangaviewer.viewmodel.MangaViewModel;

import java.util.List;

/**
 * Created by KinG on 6/18/2016.
 */
public class FavouriteMangaItemAdapter extends RecyclerView.Adapter<FavouriteMangaItemAdapter.RecyclerViewHolders> {

    private Context context;
    private LayoutInflater mInflater = null;
    private MangaViewModel viewModel;
    private List<? extends MangaMenuItem> menu;

    public FavouriteMangaItemAdapter(Context context, MangaViewModel viewModel,
                                     List<? extends MangaMenuItem> menu) {
        this.context = context;
        this.viewModel = viewModel;
        this.menu = menu;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_favourite_manga_menu_item, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.imageView.setImageURL(this.menu.get(position), true, context.getResources().getDrawable(R.color.black));
        String title = this.menu.get(position).getTitle();
        holder.textView.setText(title);
        int count = ((FavouriteMangaMenuItem) this.menu.get(position)).getUpdateCount();
        holder.countTextView.setVisibility(View.VISIBLE);
        if (count > 0 && count <= 99) {
            holder.countTextView.setText(count + "");
        } else if (count > 99) {
            holder.countTextView.setText("99+");
        } else {
            holder.countTextView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return menu != null ? menu.size() : 0;
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
            ((FavouriteMangaMenuItem) menu.get(menuPos)).setUpdateCount(0);
            FavouriteMangaDataSource dataSource = new FavouriteMangaDataSource(context);
            dataSource.updateToFavourite((FavouriteMangaMenuItem) menu.get(menuPos));
            viewModel.setSelectedMangaMenuItem(menu.get(menuPos));
            context.startActivity(new Intent(context, MangaChapterActivity.class));
            ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);

        }
    }
}
