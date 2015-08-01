package com.king.mangaviewer.common.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.king.mangaviewer.actviity.MyApplication;
import com.king.mangaviewer.common.Constants.SaveType;
import com.king.mangaviewer.common.MangaPattern.PatternFactory;
import com.king.mangaviewer.common.MangaPattern.WebSiteBasePattern;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.model.TitleAndUrl;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.util.ArrayList;
import java.util.List;

public class MangaHelper {
    String menuHtml = "";
    private Context context;

    public MangaHelper(Context context) {
        this.context = context;
        // WebType = setting;
    }

    private SettingViewModel getSettingViewModel() {
        return ((MyApplication) context.getApplicationContext()).AppViewModel.Setting;
    }
    private String getMenuHtml() {
        if (menuHtml.equalsIgnoreCase("")) {
            WebSiteBasePattern pattern = PatternFactory.getPattern(context,
                    getSettingViewModel().getSelectedWebSite());
            return pattern.GetHtml(pattern.WEBSITEURL);
        } else {
            return menuHtml;
        }

    }

    /* Page */
    public String GetIamgeByImageUrl(MangaPageItem page) {
        return null;

    }

    public List<MangaPageItem> GetPageList(MangaChapterItem chapter) {

        WebSiteBasePattern mPattern = PatternFactory.getPattern(context,
                getSettingViewModel().getSelectedWebSite());
        List<String> pageUrlList = mPattern.GetPageList(chapter.getUrl());
        List<MangaPageItem> mangaPageList = new ArrayList<MangaPageItem>();

        for (int i = 0; i < pageUrlList.size(); i++) {

            MangaPageItem item = new MangaPageItem("page-" + i, null, null,
                    null, pageUrlList.get(i), chapter, i,
                    pageUrlList.size());
            item.setWebImageUrl(mPattern.GetImageUrl(item.getUrl(),
                    item.getNowNum()));
            mangaPageList.add(item);

        }
        return mangaPageList;

    }

    /* Chapter */
    public List<MangaChapterItem> getChapterList(MangaMenuItem menu) {
        WebSiteBasePattern mPattern = PatternFactory.getPattern(context,
                getSettingViewModel().getSelectedWebSite());

        List<TitleAndUrl> tauList = mPattern.GetChapterList(menu.getUrl());

        List<MangaChapterItem> list = new ArrayList<MangaChapterItem>();
        for (int i = 0; i < tauList.size(); i++) {
            list.add(new MangaChapterItem("Chapter-" + i, tauList.get(i)
                    .getTitle(), null, tauList.get(i).getImagePath(),
                    tauList.get(i).getUrl(), menu));
        }

        return list;
    }

    /* Menu */
    public List<MangaMenuItem> GetNewMangeList() {
        WebSiteBasePattern mPattern = PatternFactory.getPattern(context,
                getSettingViewModel().getSelectedWebSite());
        String html = getMenuHtml();
        List<TitleAndUrl> pageUrlList = mPattern.GetTopMangaList(html);

        List<MangaMenuItem> menuList = new ArrayList<MangaMenuItem>();
        for (int i = 0; i < pageUrlList.size(); i++) {
            menuList.add(new MangaMenuItem("Menu-" + i, pageUrlList.get(i)
                    .getTitle(), null, pageUrlList.get(i).getImagePath(),
                    pageUrlList.get(i).getUrl()));
        }

        return menuList;
    }

    /* Search */
    public List<MangaMenuItem> GetSearchMangeList(String query, int pageNum) {
        WebSiteBasePattern mPattern = PatternFactory.getPattern(context,
                getSettingViewModel().getSelectedWebSite());
        String html = getMenuHtml();
        List<TitleAndUrl> pageUrlList = mPattern.GetSearchingList(query, pageNum);

        List<MangaMenuItem> menuList = new ArrayList<MangaMenuItem>();
        for (int i = 0; i < pageUrlList.size(); i++) {
            menuList.add(new MangaMenuItem("Menu-" + i, pageUrlList.get(i)
                    .getTitle(), null, pageUrlList.get(i).getImagePath(),
                    pageUrlList.get(i).getUrl()));
        }

        return menuList;
    }

    public Drawable getPageImage(final MangaPageItem page, final ImageView imageView, final GetImageCallback imageCallback) {
        final String imageUrl = page.getImagePath();
        if (imageUrl != null && imageUrl != "") {
            //从磁盘中获取
            Drawable drawable = Drawable.createFromPath(imageUrl);
            return drawable;

        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageView, imageUrl);
            }
        };
        //建立新一个新的线程下载图片
        new Thread() {
            @Override
            public void run() {
                WebSiteBasePattern mPattern = PatternFactory.getPattern(context,
                        getSettingViewModel().getSelectedWebSite());
                String tmpPath = mPattern.DownloadImgPage(page.getWebImageUrl(), page, SaveType.Temp, page.getUrl());
                page.setImagePath(tmpPath);
                Drawable drawable = Drawable.createFromPath(tmpPath);
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        }.start();
        return null;
    }

    public interface GetImageCallback {
        public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl);
    }
}
