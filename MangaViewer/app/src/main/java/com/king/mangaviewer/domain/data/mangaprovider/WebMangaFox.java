package com.king.mangaviewer.domain.data.mangaprovider;

import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import java.util.Collections;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KinG on 8/31/2015.
 */
public class WebMangaFox extends MangaProvider {


    private static String mRestSearchString = "&type=&author_method=cw&author=&artist_method=cw&artist=&genres%5BAction%5D=0&genres%5BAdult%5D=0&genres%5BAdventure%5D=0&genres%5BComedy%5D=0&genres%5BDoujinshi%5D=0&genres%5BDrama%5D=0&genres%5BEcchi%5D=0&genres%5BFantasy%5D=0&genres%5BGender+Bender%5D=0&genres%5BHarem%5D=0&genres%5BHistorical%5D=0&genres%5BHorror%5D=0&genres%5BJosei%5D=0&genres%5BMartial+Arts%5D=0&genres%5BMature%5D=0&genres%5BMecha%5D=0&genres%5BMystery%5D=0&genres%5BOne+Shot%5D=0&genres%5BPsychological%5D=0&genres%5BRomance%5D=0&genres%5BSchool+Life%5D=0&genres%5BSci-fi%5D=0&genres%5BSeinen%5D=0&genres%5BShoujo%5D=0&genres%5BShoujo+Ai%5D=0&genres%5BShounen%5D=0&genres%5BShounen+Ai%5D=0&genres%5BSlice+of+Life%5D=0&genres%5BSmut%5D=0&genres%5BSports%5D=0&genres%5BSupernatural%5D=0&genres%5BTragedy%5D=0&genres%5BWebtoons%5D=0&genres%5BYaoi%5D=0&genres%5BYuri%5D=0&released_method=eq&released=&rating_method=eq&rating=&is_completed=&advopts=1&sort=views&order=za";
    private static String LOG_TAG = "WebMangaFox";

    public WebMangaFox() {
        setWEBSITE_URL("https://fanfox.net/");
        setLatestMangaUrl("https://fanfox.net/releases/");
        setWEB_SEARCH_URL("https://fanfox.net/search.php?name_method=cw&name=%s&page=%d%s");
        setWEB_ALL_MANGA_BASE_URL("https://fanfox.net/directory/%d.htm");
        setCHARSET("utf8");
    }

    //Menu
    @Override
    protected List<TitleAndUrl> getLatestMangaList(String html) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();

        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".manga-list-4-list > li > a");
        for (Element e : el) {
            String url = checkUrl(e.attr("title"));
            String title = e.text();
            String imageUrl = e.select("img").attr("src");
            topMangaList.add(new TitleAndUrl(title, url, imageUrl));
        }

        return topMangaList;

    }


    @Override
    protected List<TitleAndUrl> getAllMangaList(String html) {
        List<TitleAndUrl> mangaList = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".list li");
        for (int i = 0; i < el.size(); i++) {
            String title = el.get(i).select(".title").text();
            String url = checkUrl(el.get(i).select(".title").attr("href"));
            String imageUrl = el.get(i).select(".manga_img img").attr("src");
            mangaList.add(new TitleAndUrl(title, url, imageUrl));
        }
        return mangaList;
    }

    @Override
    protected List<TitleAndUrl> getSearchList(String html) {
        List<TitleAndUrl> mangaList = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".series_preview");
        for (int i = 0; i < el.size(); i++) {
            String title = el.get(i).text();
            String url = checkUrl(el.get(i).attr("href"));
            mangaList.add(new TitleAndUrl(title, url));
        }
        return mangaList;
    }

    @Override
    protected int getSearchTotalNum(String html) {
        Document doc = Jsoup.parse(html);
        Elements els = doc.select("#nav ul li");
        int index = els.size() - 2;
        return Integer.parseInt(els.get(index).text());
    }

    @Override
    protected int getAllMangaTotalNum(String html) {
        return getSearchTotalNum(html);
    }

    @Override
    protected String getSearchUrl(String queryText, int pageNum) {
        return String.format(getWEB_SEARCH_URL(), queryText, pageNum, mRestSearchString);
    }

    //Chapter

    @Override
    public List<TitleAndUrl> getChapterList(String chapterUrl) {
        String html = getHtml(chapterUrl);
        List<TitleAndUrl> list = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements els = doc.select(".detail-main-list li a");
        for (Element e : els) {
            String url = checkUrl(e.attr("href"));
            String title = e.attr("title");
            list.add(new TitleAndUrl(title, url));
        }
        Collections.reverse(list);
        return list;

    }


    //Page
    @Override
    public List<String> getPageList(String firstPageUrl) {
        List<String> pageList = new ArrayList<>();
        String html = getHtml(firstPageUrl);
        int total = getTotalNum(html);

        String fileName = firstPageUrl.substring(firstPageUrl.lastIndexOf("/"));
        String preFileName = firstPageUrl.substring(0, firstPageUrl.lastIndexOf("/"));

        if (fileName.isEmpty()) {
            for (int i = 1; i <= total; i++) {
                pageList.add(preFileName + i + ".html");
            }
        } else {
            for (int i = 1; i <= total; i++) {
                pageList.add(preFileName + fileName.replace("1", i + ""));
            }
        }
        return pageList;
    }

    @Override
    public String getImageUrl(String pageUrl, int nowPage) {
        String html = getHtml(pageUrl);
        Document doc = Jsoup.parse(html);
        return doc.select("#image").attr("src");
    }


}
