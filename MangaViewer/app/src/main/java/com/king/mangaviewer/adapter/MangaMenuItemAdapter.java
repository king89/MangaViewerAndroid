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
import java.util.List;

public class MangaMenuItemAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater = null;
    private MangaViewModel viewModel;
    private AsyncImageLoader asyncImageLoader = null;
    private List<? extends MangaMenuItem> menu;
    private boolean isFavouriteMangaMenu;

    public MangaMenuItemAdapter(Context context, MangaViewModel viewModel,
                                List<? extends MangaMenuItem> menu) {
        this(context, viewModel, menu, false);
    }

    public MangaMenuItemAdapter(Context context, MangaViewModel viewModel,
                                List<? extends MangaMenuItem> menu, boolean isFavouriteMangaMenu) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.viewModel = viewModel;
        this.context = context;
        this.menu = menu;

        asyncImageLoader = AsyncImageLoader.getInstance();
        this.isFavouriteMangaMenu = isFavouriteMangaMenu;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return menu.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        Object result = null;
        try {
            result = menu.get(position);
        } catch (Exception e) {
            // TODO: handle exception
            result = null;
        }
        return result;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        //alway new a ViewHolder
        holder = new ViewHolder();

        if (isFavouriteMangaMenu) {
            //Favourite Manga Menu
            convertView = mInflater.inflate(R.layout.list_favourite_manga_menu_item, null);
            holder.imageView = (MyImageView) convertView.findViewById(R.id.imageView);
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            holder.countTextView = (TextView) convertView.findViewById(R.id.countTextView);
            int count = ((FavouriteMangaMenuItem) this.menu.get(position)).getUpdateCount();
            if (count > 0 && count <= 99) {
                holder.countTextView.setText(count + "");
            }else if (count > 99){
                holder.countTextView.setText("99+");
            }else {
                holder.countTextView.setVisibility(View.INVISIBLE);
            }
        } else {
            convertView = mInflater.inflate(R.layout.list_manga_menu_item, null);
            holder.imageView = (MyImageView) convertView.findViewById(R.id.imageView);
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
        }
        convertView.setTag(holder);

        holder.imageView.setImageURL(this.menu.get(position), true, context.getResources().getDrawable(R.color.black));
        String title = this.menu.get(position).getTitle();
        holder.textView.setText(title);
        final int menuPos = position;
        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //Toast.makeText(context, "Item", Toast.LENGTH_SHORT).show();
                if (isFavouriteMangaMenu){
                    ((FavouriteMangaMenuItem)menu.get(menuPos)).setUpdateCount(0);
                    FavouriteMangaDataSource dataSource = new FavouriteMangaDataSource(context);
                    dataSource.updateToFavourite((FavouriteMangaMenuItem)menu.get(menuPos));
                }
                viewModel.setSelectedMangaMenuItem(menu.get(menuPos));
                context.startActivity(new Intent(context, MangaChapterActivity.class));
                ((Activity) context).overridePendingTransition(R.anim.in_rightleft, R.anim.out_rightleft);
            }
        });
        return convertView;

    }

    class ViewHolder {
        public MyImageView imageView;
        public TextView textView;
        public TextView countTextView;
    }

}
