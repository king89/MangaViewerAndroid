package com.king.mangaviewer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.king.mangaviewer.domain.external.mangaprovider.MangaProvider;
import com.king.mangaviewer.MyApplication;
import com.king.mangaviewer.common.Constants.SaveType;
import com.king.mangaviewer.domain.external.mangaprovider.LocalMangaProvider;
import com.king.mangaviewer.domain.external.mangaprovider.ProviderFactory;
import com.king.mangaviewer.model.MangaChapterItem;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.MangaPageItem;
import com.king.mangaviewer.model.TitleAndUrl;
import com.king.mangaviewer.viewmodel.SettingViewModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Deprecated
public class MangaHelper {
    String menuHtml = "";
    private Context context;
    private ProviderFactory providerFactory;
    public MangaHelper(Context context) {
        this.context = context;
        providerFactory = MyApplication.INSTANCE.component.providerFactory();
        // WebType = setting;
    }

    private SettingViewModel getSettingViewModel() {
        return ((MyApplication) context.getApplicationContext()).getAppViewModel().Setting;
    }

    private String getMenuHtml(MangaProvider pattern) {
        if (menuHtml.equalsIgnoreCase("")) {
            return pattern.getHtml(pattern.getWEBSITE_URL());
        } else {
            return menuHtml;
        }

    }

    /* Page */
    public String getWebImageUrl(MangaPageItem page) {
        MangaProvider mPattern = providerFactory.getPattern(page.getMangaWebSource());
        page.setWebImageUrl(mPattern.getImageUrl(page.getUrl(),
                page.getNowNum()));
        return page.getWebImageUrl();

    }

    public List<MangaPageItem> GetPageList(MangaChapterItem chapter) {
        MangaProvider mPattern = providerFactory.getPattern(chapter.getMangaWebSource());
        List<String> pageUrlList = mPattern.getPageList(chapter.getUrl());
        List<MangaPageItem> mangaPageList = new ArrayList<MangaPageItem>();
        if (pageUrlList != null) {
            for (int i = 0; i < pageUrlList.size(); i++) {
                MangaPageItem item = new MangaPageItem("page-" + i, null, null,
                        null, pageUrlList.get(i), chapter, i,
                        pageUrlList.size());
                item.setReferUrl(chapter.getUrl());
                mangaPageList.add(item);
            }
        }
        return mangaPageList;
    }

    public Drawable getPageImage(final MangaPageItem page, final ImageView imageView,
            final GetImageCallback imageCallback) {
        //For Local Manga
        if (page.getMangaWebSource().getClassName() == LocalMangaProvider.class.getName()) {
            ZipFile zf = null;
            try {
                zf = new ZipFile(page.getChapter().getUrl());
                ZipEntry ze = zf.getEntry(page.getUrl());
                Bitmap img = BitmapFactory.decodeStream(zf.getInputStream(ze));
                return new BitmapDrawable(img);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            //get pre page image file path, if exist just use it, if not : download
            final MangaProvider mPattern = providerFactory.getPattern(page.getMangaWebSource());

            if (!page.getWebImageUrl().isEmpty()) {
                final String imageUrl = mPattern.getPrePageImageFilePath(page.getWebImageUrl(),
                        page);
                if (!imageUrl.isEmpty() && (new File(imageUrl).exists())) {
                    //从磁盘中获取
                    Drawable drawable = Drawable.createFromPath(imageUrl);
                    return drawable;
                }
            }
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    imageCallback.imageLoaded((Drawable) message.obj, imageView, null);
                }
            };
            //建立新一个新的线程下载图片
            new Thread() {
                @Override
                public void run() {
                    if (page.getWebImageUrl().isEmpty()) {
                        page.setWebImageUrl(mPattern.getImageUrl(page.getUrl(),
                                page.getNowNum()));
                    }
                    String tmpPath = mPattern.DownloadImgPage(page.getWebImageUrl(), page,
                            SaveType.Temp, page.getReferUrl());
                    page.setImagePath(tmpPath);
                    Drawable drawable = Drawable.createFromPath(tmpPath);
                    Message message = handler.obtainMessage(0, drawable);
                    handler.sendMessage(message);
                }
            }.start();
            return null;
        }
    }

    /* Chapter */
    public List<MangaChapterItem> getChapterList(MangaMenuItem menu) {
        MangaProvider mPattern = providerFactory.getPattern(menu.getMangaWebSource());

        List<TitleAndUrl> tauList = mPattern.getChapterList(menu.getUrl());
        List<MangaChapterItem> list = new ArrayList<MangaChapterItem>();
        if (tauList != null) {
            for (int i = 0; i < tauList.size(); i++) {
                list.add(new MangaChapterItem("Chapter-" + i, tauList.get(i)
                        .getTitle(), "", tauList.get(i).getImagePath(),
                        tauList.get(i).getUrl(), menu));
            }
        }
        return list;
    }

    public List<MangaMenuItem> getAllManga(List<MangaMenuItem> mangaList,
            HashMap<String, Object> state) {
        MangaProvider mPattern = providerFactory.getPattern(
                getSettingViewModel().getSelectedWebSource());

        List<TitleAndUrl> pageUrlList = mPattern.getAllMangaList(state);
        if (pageUrlList != null) {
            for (int i = 0; i < pageUrlList.size(); i++) {
                mangaList.add(new MangaMenuItem("Menu-" + i, pageUrlList.get(i)
                        .getTitle(), "", pageUrlList.get(i).getImagePath(),
                        pageUrlList.get(i).getUrl(),
                        getSettingViewModel().getSelectedWebSource()));
            }
        }
        return mangaList;
    }

    /* Search */
    public List<MangaMenuItem> getSearchMangeList(List<MangaMenuItem> mangaList,
            HashMap<String, Object> state) {
        MangaProvider mPattern = providerFactory.getPattern(
                getSettingViewModel().getSelectedWebSource());
        List<TitleAndUrl> pageUrlList = mPattern.getSearchingList(state);
        if (pageUrlList != null) {
            for (int i = 0; i < pageUrlList.size(); i++) {
                mangaList.add(new MangaMenuItem("Menu-" + i, pageUrlList.get(i)
                        .getTitle(), "", pageUrlList.get(i).getImagePath(),
                        pageUrlList.get(i).getUrl(),
                        getSettingViewModel().getSelectedWebSource()));
            }
        }
        return mangaList;
    }

    public interface GetImageCallback {
        public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl);
    }


}
