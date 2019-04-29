package com.king.mangaviewer.domain.external.mangaprovider;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.king.mangaviewer.model.MangaMenuItem;
import com.king.mangaviewer.model.TitleAndUrl;

import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liang on 10/25/2016.
 */

public class WebDMZJ extends MangaProvider {
    private static final String TAG = WebDMZJ.class.getSimpleName();
    String IMAGEURL = "https://images.dmzj.com/";

    @Inject
    public WebDMZJ() {
        setWEBSITE_URL("https://manhua.dmzj.com/");
        setWEB_SEARCH_URL("http://s.acg.dmzj.com/comicsum/search.php?s=%s&p=%s");
        setLatestMangaUrl("https://manhua.dmzj.com/update_1.shtml");
        setWEB_ALL_MANGA_BASE_URL(
                "http://s.acg.dmzj.com/mh/index.php?c=category&m=doSearch&status=0&reader_group=0&zone=0&initial=all&type=0&p=%s&callback=search.renderResult");
        setCHARSET("utf-8");
    }

    @Override
    protected List<TitleAndUrl> getAllMangaList(String html) {
        List<TitleAndUrl> list = new ArrayList<TitleAndUrl>();
        String json = html.substring(html.indexOf("{"), html.lastIndexOf("}") + 1);
        JSONObject jsonList = null;
        try {
            jsonList = new JSONObject(json);
            JSONArray item = jsonList.getJSONArray("result");
            for (int i = 0; i < item.length(); i++) {
                String name = item.getJSONObject(i).getString("name");
                String url = checkUrl(item.getJSONObject(i).getString("comic_url"));
                String imageUrl = item.getJSONObject(i).getString("comic_cover");
                list.add(new TitleAndUrl(name, url, imageUrl));
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected int getAllMangaTotalNum(String html) {
        String json = html.substring(html.indexOf("{"), html.lastIndexOf("}") + 1);
        JSONObject jsonList = null;
        try {
            jsonList = new JSONObject(json);
            int total = jsonList.getInt("page_count");
            return total;
        } catch (Exception e) {
            return 0;
        }
    }

    @NotNull @Override
    protected List<MangaMenuItem> getLatestMangaList(@NotNull String html) {
        List<TitleAndUrl> topMangaList = new ArrayList<TitleAndUrl>();
        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".boxdiv1");
        Iterator i = el.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = checkUrl(e.select(".pictextst ").attr("href"));
            String title = e.select(".pictextst").text();
            String imageUrl = e.select(".picborder a img").attr("src");
            topMangaList.add(new TitleAndUrl(title, url, imageUrl));
        }
        return toMenuItem(topMangaList);
    }

    @Nullable @Override
    public List<TitleAndUrl> getChapterList(@NotNull MangaMenuItem menu) {
        List<TitleAndUrl> list = new ArrayList<TitleAndUrl>();
        String html = getHtml(menu.getUrl());
        Document doc = Jsoup.parse(html);
        Elements el = doc.select(".cartoon_online_border ul li");
        Iterator i = el.iterator();
        while (i.hasNext()) {
            Element e = (Element) i.next();
            String url = checkUrl(e.select("a").attr("href"));
            String title = e.select("a").attr("title");
            String imageUrl = "";
            list.add(new TitleAndUrl(title, url, imageUrl));
        }
        return list;
    }

    @Override
    protected List<TitleAndUrl> getSearchList(String html) {
        List<TitleAndUrl> list = new ArrayList<TitleAndUrl>();
        String json = html.substring(html.indexOf("["), html.lastIndexOf("]") + 1);
        Type listType = new TypeToken<ArrayList<DMZJMangaItem>>() {
        }.getType();
        List<DMZJMangaItem> jsonList = new Gson().fromJson(json, listType);
        for (DMZJMangaItem item : jsonList) {
            list.add(new TitleAndUrl(item.getName(), item.getComicUrl(),
                    checkUrl(item.getComicCover(), true)));
        }
        return list;
    }

    @Override
    public List<String> getPageList(String firstPageUrl) {
        String html = getHtml(firstPageUrl);
        Pattern r = Pattern.compile(
                "(eval\\(.+?\\[)(\".+?)(\\].+?,)(\\d+),(\\d+),(\'.+?\').+?(\\{\\}\\)\\))");
        try {
            Matcher m = r.matcher(html);
            m.find();
            String result = m.group(2);
            int num1 = Integer.parseInt(m.group(4));
            int num2 = Integer.parseInt(m.group(5));
            String strReplace = m.group(6);
            String[] names = strReplace.replace("'", "").split("\\|");

            List<String> list = Arrays.asList(result.split(","));
            String pattern = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (int i = 0; i < list.size(); i++) {
                String tmp = list.get(i).replace("\"", "").replace("\\\\", "");
                StringBuilder sb = new StringBuilder();
                for (int index = 0; index < tmp.length(); ) {
                    String toConvString = "";
                    if (pattern.contains(tmp.charAt(index) + "")) {
                        // collect all the character
                        while (index < tmp.length() && (pattern.contains(tmp.charAt(index) + ""))) {
                            toConvString += tmp.charAt(index);
                            index++;
                        }
                        int replaceNum = GetInt(toConvString, num1, num2);
                        String replace = names[replaceNum];
                        if (replace.equals("")) {
                            replace = toConvString;
                        }
                        sb.append(replace);
                    } else {
                        sb.append(tmp.charAt(index));
                        index++;
                    }
                }
                list.set(i, IMAGEURL + sb.toString());
            }
            return list;

        } catch (Exception e) {
            Log.e(TAG, "getPageList", e);
            return null;
        }
    }

    @Override
    public String getImageUrl(String pageUrl, int nowPage) {
        return checkUrl(pageUrl);
    }

    private int GetInt(String s, int num1, int num2) {
        int result = 0;
        try {
            for (Character c : s.toCharArray()) {
                result = Integer.parseInt(c.toString(), 36) + result * num1;
                if (Character.isUpperCase(c)) {
                    result += 26;
                }
            }
            return result;
        } catch (Exception e) {
            Log.d(TAG, "", e);
        }
        return result;
    }

    class DMZJMangaItem {
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("alias_name")
        @Expose
        private String aliasName;
        @SerializedName("real_name")
        @Expose
        private String realName;
        @SerializedName("publish")
        @Expose
        private Object publish;
        @SerializedName("type")
        @Expose
        private Object type;
        @SerializedName("zone")
        @Expose
        private String zone;
        @SerializedName("zone_tag_id")
        @Expose
        private String zoneTagId;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("status_tag_id")
        @Expose
        private String statusTagId;
        @SerializedName("last_update_chapter_name")
        @Expose
        private String lastUpdateChapterName;
        @SerializedName("last_update_chapter_id")
        @Expose
        private String lastUpdateChapterId;
        @SerializedName("last_updatetime")
        @Expose
        private String lastUpdatetime;
        @SerializedName("check")
        @Expose
        private String check;
        @SerializedName("chapters_tbl")
        @Expose
        private String chaptersTbl;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("hidden")
        @Expose
        private String hidden;
        @SerializedName("cover")
        @Expose
        private String cover;
        @SerializedName("sum_chapters")
        @Expose
        private String sumChapters;
        @SerializedName("sum_source")
        @Expose
        private String sumSource;
        @SerializedName("hot_search")
        @Expose
        private String hotSearch;
        @SerializedName("hot_hits")
        @Expose
        private String hotHits;
        @SerializedName("first_letter")
        @Expose
        private String firstLetter;
        @SerializedName("keywords")
        @Expose
        private String keywords;
        @SerializedName("comic_py")
        @Expose
        private String comicPy;
        @SerializedName("introduction")
        @Expose
        private String introduction;
        @SerializedName("addtime")
        @Expose
        private String addtime;
        @SerializedName("authors")
        @Expose
        private String authors;
        @SerializedName("types")
        @Expose
        private String types;
        @SerializedName("series")
        @Expose
        private String series;
        @SerializedName("need_update")
        @Expose
        private String needUpdate;
        @SerializedName("update_notice")
        @Expose
        private String updateNotice;
        @SerializedName("readergroup")
        @Expose
        private String readergroup;
        @SerializedName("readergroup_tag_id")
        @Expose
        private String readergroupTagId;
        @SerializedName("has_comment_id")
        @Expose
        private String hasCommentId;
        @SerializedName("comment_key")
        @Expose
        private String commentKey;
        @SerializedName("day_click_count")
        @Expose
        private String dayClickCount;
        @SerializedName("week_click_count")
        @Expose
        private String weekClickCount;
        @SerializedName("month_click_count")
        @Expose
        private String monthClickCount;
        @SerializedName("page_show_flag")
        @Expose
        private String pageShowFlag;
        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("source")
        @Expose
        private String source;
        @SerializedName("grade")
        @Expose
        private String grade;
        @SerializedName("copyright")
        @Expose
        private String copyright;
        @SerializedName("direction")
        @Expose
        private String direction;
        @SerializedName("token32")
        @Expose
        private String token32;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("mobile")
        @Expose
        private String mobile;
        @SerializedName("w_link")
        @Expose
        private String wLink;
        @SerializedName("app_day_click_count")
        @Expose
        private String appDayClickCount;
        @SerializedName("app_week_click_count")
        @Expose
        private String appWeekClickCount;
        @SerializedName("app_month_click_count")
        @Expose
        private String appMonthClickCount;
        @SerializedName("app_click_count")
        @Expose
        private String appClickCount;
        @SerializedName("islong")
        @Expose
        private String islong;
        @SerializedName("alading")
        @Expose
        private String alading;
        @SerializedName("uid")
        @Expose
        private Object uid;
        @SerializedName("week_add_num")
        @Expose
        private String weekAddNum;
        @SerializedName("month_add_num")
        @Expose
        private String monthAddNum;
        @SerializedName("total_add_num")
        @Expose
        private String totalAddNum;
        @SerializedName("sogou")
        @Expose
        private String sogou;
        @SerializedName("baidu_assistant")
        @Expose
        private String baiduAssistant;
        @SerializedName("is_checked")
        @Expose
        private String isChecked;
        @SerializedName("quality")
        @Expose
        private String quality;
        @SerializedName("is_show_animation_list")
        @Expose
        private String isShowAnimationList;
        @SerializedName("zone_link")
        @Expose
        private String zoneLink;
        @SerializedName("is_dmzj")
        @Expose
        private String isDmzj;
        @SerializedName("device_show")
        @Expose
        private String deviceShow;
        @SerializedName("comic_name")
        @Expose
        private String comicName;
        @SerializedName("comic_author")
        @Expose
        private String comicAuthor;
        @SerializedName("comic_cover")
        @Expose
        private String comicCover;
        @SerializedName("comic_url_raw")
        @Expose
        private String comicUrlRaw;
        @SerializedName("comic_url")
        @Expose
        private String comicUrl;
        @SerializedName("chapter_url_raw")
        @Expose
        private String chapterUrlRaw;
        @SerializedName("chapter_url")
        @Expose
        private String chapterUrl;

        /**
         * @return The id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id The id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return The aliasName
         */
        public String getAliasName() {
            return aliasName;
        }

        /**
         * @param aliasName The alias_name
         */
        public void setAliasName(String aliasName) {
            this.aliasName = aliasName;
        }

        /**
         * @return The realName
         */
        public String getRealName() {
            return realName;
        }

        /**
         * @param realName The real_name
         */
        public void setRealName(String realName) {
            this.realName = realName;
        }

        /**
         * @return The publish
         */
        public Object getPublish() {
            return publish;
        }

        /**
         * @param publish The publish
         */
        public void setPublish(Object publish) {
            this.publish = publish;
        }

        /**
         * @return The type
         */
        public Object getType() {
            return type;
        }

        /**
         * @param type The type
         */
        public void setType(Object type) {
            this.type = type;
        }

        /**
         * @return The zone
         */
        public String getZone() {
            return zone;
        }

        /**
         * @param zone The zone
         */
        public void setZone(String zone) {
            this.zone = zone;
        }

        /**
         * @return The zoneTagId
         */
        public String getZoneTagId() {
            return zoneTagId;
        }

        /**
         * @param zoneTagId The zone_tag_id
         */
        public void setZoneTagId(String zoneTagId) {
            this.zoneTagId = zoneTagId;
        }

        /**
         * @return The language
         */
        public String getLanguage() {
            return language;
        }

        /**
         * @param language The language
         */
        public void setLanguage(String language) {
            this.language = language;
        }

        /**
         * @return The status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return The statusTagId
         */
        public String getStatusTagId() {
            return statusTagId;
        }

        /**
         * @param statusTagId The status_tag_id
         */
        public void setStatusTagId(String statusTagId) {
            this.statusTagId = statusTagId;
        }

        /**
         * @return The lastUpdateChapterName
         */
        public String getLastUpdateChapterName() {
            return lastUpdateChapterName;
        }

        /**
         * @param lastUpdateChapterName The last_update_chapter_name
         */
        public void setLastUpdateChapterName(String lastUpdateChapterName) {
            this.lastUpdateChapterName = lastUpdateChapterName;
        }

        /**
         * @return The lastUpdateChapterId
         */
        public String getLastUpdateChapterId() {
            return lastUpdateChapterId;
        }

        /**
         * @param lastUpdateChapterId The last_update_chapter_id
         */
        public void setLastUpdateChapterId(String lastUpdateChapterId) {
            this.lastUpdateChapterId = lastUpdateChapterId;
        }

        /**
         * @return The lastUpdatetime
         */
        public String getLastUpdatetime() {
            return lastUpdatetime;
        }

        /**
         * @param lastUpdatetime The last_updatetime
         */
        public void setLastUpdatetime(String lastUpdatetime) {
            this.lastUpdatetime = lastUpdatetime;
        }

        /**
         * @return The check
         */
        public String getCheck() {
            return check;
        }

        /**
         * @param check The check
         */
        public void setCheck(String check) {
            this.check = check;
        }

        /**
         * @return The chaptersTbl
         */
        public String getChaptersTbl() {
            return chaptersTbl;
        }

        /**
         * @param chaptersTbl The chapters_tbl
         */
        public void setChaptersTbl(String chaptersTbl) {
            this.chaptersTbl = chaptersTbl;
        }

        /**
         * @return The description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description The description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return The hidden
         */
        public String getHidden() {
            return hidden;
        }

        /**
         * @param hidden The hidden
         */
        public void setHidden(String hidden) {
            this.hidden = hidden;
        }

        /**
         * @return The cover
         */
        public String getCover() {
            return cover;
        }

        /**
         * @param cover The cover
         */
        public void setCover(String cover) {
            this.cover = cover;
        }

        /**
         * @return The sumChapters
         */
        public String getSumChapters() {
            return sumChapters;
        }

        /**
         * @param sumChapters The sum_chapters
         */
        public void setSumChapters(String sumChapters) {
            this.sumChapters = sumChapters;
        }

        /**
         * @return The sumSource
         */
        public String getSumSource() {
            return sumSource;
        }

        /**
         * @param sumSource The sum_source
         */
        public void setSumSource(String sumSource) {
            this.sumSource = sumSource;
        }

        /**
         * @return The hotSearch
         */
        public String getHotSearch() {
            return hotSearch;
        }

        /**
         * @param hotSearch The hot_search
         */
        public void setHotSearch(String hotSearch) {
            this.hotSearch = hotSearch;
        }

        /**
         * @return The hotHits
         */
        public String getHotHits() {
            return hotHits;
        }

        /**
         * @param hotHits The hot_hits
         */
        public void setHotHits(String hotHits) {
            this.hotHits = hotHits;
        }

        /**
         * @return The firstLetter
         */
        public String getFirstLetter() {
            return firstLetter;
        }

        /**
         * @param firstLetter The first_letter
         */
        public void setFirstLetter(String firstLetter) {
            this.firstLetter = firstLetter;
        }

        /**
         * @return The keywords
         */
        public String getKeywords() {
            return keywords;
        }

        /**
         * @param keywords The keywords
         */
        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        /**
         * @return The comicPy
         */
        public String getComicPy() {
            return comicPy;
        }

        /**
         * @param comicPy The comic_py
         */
        public void setComicPy(String comicPy) {
            this.comicPy = comicPy;
        }

        /**
         * @return The introduction
         */
        public String getIntroduction() {
            return introduction;
        }

        /**
         * @param introduction The introduction
         */
        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        /**
         * @return The addtime
         */
        public String getAddtime() {
            return addtime;
        }

        /**
         * @param addtime The addtime
         */
        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        /**
         * @return The authors
         */
        public String getAuthors() {
            return authors;
        }

        /**
         * @param authors The authors
         */
        public void setAuthors(String authors) {
            this.authors = authors;
        }

        /**
         * @return The types
         */
        public String getTypes() {
            return types;
        }

        /**
         * @param types The types
         */
        public void setTypes(String types) {
            this.types = types;
        }

        /**
         * @return The series
         */
        public String getSeries() {
            return series;
        }

        /**
         * @param series The series
         */
        public void setSeries(String series) {
            this.series = series;
        }

        /**
         * @return The needUpdate
         */
        public String getNeedUpdate() {
            return needUpdate;
        }

        /**
         * @param needUpdate The need_update
         */
        public void setNeedUpdate(String needUpdate) {
            this.needUpdate = needUpdate;
        }

        /**
         * @return The updateNotice
         */
        public String getUpdateNotice() {
            return updateNotice;
        }

        /**
         * @param updateNotice The update_notice
         */
        public void setUpdateNotice(String updateNotice) {
            this.updateNotice = updateNotice;
        }

        /**
         * @return The readergroup
         */
        public String getReadergroup() {
            return readergroup;
        }

        /**
         * @param readergroup The readergroup
         */
        public void setReadergroup(String readergroup) {
            this.readergroup = readergroup;
        }

        /**
         * @return The readergroupTagId
         */
        public String getReadergroupTagId() {
            return readergroupTagId;
        }

        /**
         * @param readergroupTagId The readergroup_tag_id
         */
        public void setReadergroupTagId(String readergroupTagId) {
            this.readergroupTagId = readergroupTagId;
        }

        /**
         * @return The hasCommentId
         */
        public String getHasCommentId() {
            return hasCommentId;
        }

        /**
         * @param hasCommentId The has_comment_id
         */
        public void setHasCommentId(String hasCommentId) {
            this.hasCommentId = hasCommentId;
        }

        /**
         * @return The commentKey
         */
        public String getCommentKey() {
            return commentKey;
        }

        /**
         * @param commentKey The comment_key
         */
        public void setCommentKey(String commentKey) {
            this.commentKey = commentKey;
        }

        /**
         * @return The dayClickCount
         */
        public String getDayClickCount() {
            return dayClickCount;
        }

        /**
         * @param dayClickCount The day_click_count
         */
        public void setDayClickCount(String dayClickCount) {
            this.dayClickCount = dayClickCount;
        }

        /**
         * @return The weekClickCount
         */
        public String getWeekClickCount() {
            return weekClickCount;
        }

        /**
         * @param weekClickCount The week_click_count
         */
        public void setWeekClickCount(String weekClickCount) {
            this.weekClickCount = weekClickCount;
        }

        /**
         * @return The monthClickCount
         */
        public String getMonthClickCount() {
            return monthClickCount;
        }

        /**
         * @param monthClickCount The month_click_count
         */
        public void setMonthClickCount(String monthClickCount) {
            this.monthClickCount = monthClickCount;
        }

        /**
         * @return The pageShowFlag
         */
        public String getPageShowFlag() {
            return pageShowFlag;
        }

        /**
         * @param pageShowFlag The page_show_flag
         */
        public void setPageShowFlag(String pageShowFlag) {
            this.pageShowFlag = pageShowFlag;
        }

        /**
         * @return The token
         */
        public String getToken() {
            return token;
        }

        /**
         * @param token The token
         */
        public void setToken(String token) {
            this.token = token;
        }

        /**
         * @return The source
         */
        public String getSource() {
            return source;
        }

        /**
         * @param source The source
         */
        public void setSource(String source) {
            this.source = source;
        }

        /**
         * @return The grade
         */
        public String getGrade() {
            return grade;
        }

        /**
         * @param grade The grade
         */
        public void setGrade(String grade) {
            this.grade = grade;
        }

        /**
         * @return The copyright
         */
        public String getCopyright() {
            return copyright;
        }

        /**
         * @param copyright The copyright
         */
        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        /**
         * @return The direction
         */
        public String getDirection() {
            return direction;
        }

        /**
         * @param direction The direction
         */
        public void setDirection(String direction) {
            this.direction = direction;
        }

        /**
         * @return The token32
         */
        public String getToken32() {
            return token32;
        }

        /**
         * @param token32 The token32
         */
        public void setToken32(String token32) {
            this.token32 = token32;
        }

        /**
         * @return The url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url The url
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return The mobile
         */
        public String getMobile() {
            return mobile;
        }

        /**
         * @param mobile The mobile
         */
        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        /**
         * @return The wLink
         */
        public String getWLink() {
            return wLink;
        }

        /**
         * @param wLink The w_link
         */
        public void setWLink(String wLink) {
            this.wLink = wLink;
        }

        /**
         * @return The appDayClickCount
         */
        public String getAppDayClickCount() {
            return appDayClickCount;
        }

        /**
         * @param appDayClickCount The app_day_click_count
         */
        public void setAppDayClickCount(String appDayClickCount) {
            this.appDayClickCount = appDayClickCount;
        }

        /**
         * @return The appWeekClickCount
         */
        public String getAppWeekClickCount() {
            return appWeekClickCount;
        }

        /**
         * @param appWeekClickCount The app_week_click_count
         */
        public void setAppWeekClickCount(String appWeekClickCount) {
            this.appWeekClickCount = appWeekClickCount;
        }

        /**
         * @return The appMonthClickCount
         */
        public String getAppMonthClickCount() {
            return appMonthClickCount;
        }

        /**
         * @param appMonthClickCount The app_month_click_count
         */
        public void setAppMonthClickCount(String appMonthClickCount) {
            this.appMonthClickCount = appMonthClickCount;
        }

        /**
         * @return The appClickCount
         */
        public String getAppClickCount() {
            return appClickCount;
        }

        /**
         * @param appClickCount The app_click_count
         */
        public void setAppClickCount(String appClickCount) {
            this.appClickCount = appClickCount;
        }

        /**
         * @return The islong
         */
        public String getIslong() {
            return islong;
        }

        /**
         * @param islong The islong
         */
        public void setIslong(String islong) {
            this.islong = islong;
        }

        /**
         * @return The alading
         */
        public String getAlading() {
            return alading;
        }

        /**
         * @param alading The alading
         */
        public void setAlading(String alading) {
            this.alading = alading;
        }

        /**
         * @return The uid
         */
        public Object getUid() {
            return uid;
        }

        /**
         * @param uid The uid
         */
        public void setUid(Object uid) {
            this.uid = uid;
        }

        /**
         * @return The weekAddNum
         */
        public String getWeekAddNum() {
            return weekAddNum;
        }

        /**
         * @param weekAddNum The week_add_num
         */
        public void setWeekAddNum(String weekAddNum) {
            this.weekAddNum = weekAddNum;
        }

        /**
         * @return The monthAddNum
         */
        public String getMonthAddNum() {
            return monthAddNum;
        }

        /**
         * @param monthAddNum The month_add_num
         */
        public void setMonthAddNum(String monthAddNum) {
            this.monthAddNum = monthAddNum;
        }

        /**
         * @return The totalAddNum
         */
        public String getTotalAddNum() {
            return totalAddNum;
        }

        /**
         * @param totalAddNum The total_add_num
         */
        public void setTotalAddNum(String totalAddNum) {
            this.totalAddNum = totalAddNum;
        }

        /**
         * @return The sogou
         */
        public String getSogou() {
            return sogou;
        }

        /**
         * @param sogou The sogou
         */
        public void setSogou(String sogou) {
            this.sogou = sogou;
        }

        /**
         * @return The baiduAssistant
         */
        public String getBaiduAssistant() {
            return baiduAssistant;
        }

        /**
         * @param baiduAssistant The baidu_assistant
         */
        public void setBaiduAssistant(String baiduAssistant) {
            this.baiduAssistant = baiduAssistant;
        }

        /**
         * @return The isChecked
         */
        public String getIsChecked() {
            return isChecked;
        }

        /**
         * @param isChecked The is_checked
         */
        public void setIsChecked(String isChecked) {
            this.isChecked = isChecked;
        }

        /**
         * @return The quality
         */
        public String getQuality() {
            return quality;
        }

        /**
         * @param quality The quality
         */
        public void setQuality(String quality) {
            this.quality = quality;
        }

        /**
         * @return The isShowAnimationList
         */
        public String getIsShowAnimationList() {
            return isShowAnimationList;
        }

        /**
         * @param isShowAnimationList The is_show_animation_list
         */
        public void setIsShowAnimationList(String isShowAnimationList) {
            this.isShowAnimationList = isShowAnimationList;
        }

        /**
         * @return The zoneLink
         */
        public String getZoneLink() {
            return zoneLink;
        }

        /**
         * @param zoneLink The zone_link
         */
        public void setZoneLink(String zoneLink) {
            this.zoneLink = zoneLink;
        }

        /**
         * @return The isDmzj
         */
        public String getIsDmzj() {
            return isDmzj;
        }

        /**
         * @param isDmzj The is_dmzj
         */
        public void setIsDmzj(String isDmzj) {
            this.isDmzj = isDmzj;
        }

        /**
         * @return The deviceShow
         */
        public String getDeviceShow() {
            return deviceShow;
        }

        /**
         * @param deviceShow The device_show
         */
        public void setDeviceShow(String deviceShow) {
            this.deviceShow = deviceShow;
        }

        /**
         * @return The comicName
         */
        public String getComicName() {
            return comicName;
        }

        /**
         * @param comicName The comic_name
         */
        public void setComicName(String comicName) {
            this.comicName = comicName;
        }

        /**
         * @return The comicAuthor
         */
        public String getComicAuthor() {
            return comicAuthor;
        }

        /**
         * @param comicAuthor The comic_author
         */
        public void setComicAuthor(String comicAuthor) {
            this.comicAuthor = comicAuthor;
        }

        /**
         * @return The comicCover
         */
        public String getComicCover() {
            return comicCover;
        }

        /**
         * @param comicCover The comic_cover
         */
        public void setComicCover(String comicCover) {
            this.comicCover = comicCover;
        }

        /**
         * @return The comicUrlRaw
         */
        public String getComicUrlRaw() {
            return comicUrlRaw;
        }

        /**
         * @param comicUrlRaw The comic_url_raw
         */
        public void setComicUrlRaw(String comicUrlRaw) {
            this.comicUrlRaw = comicUrlRaw;
        }

        /**
         * @return The comicUrl
         */
        public String getComicUrl() {
            return comicUrl;
        }

        /**
         * @param comicUrl The comic_url
         */
        public void setComicUrl(String comicUrl) {
            this.comicUrl = comicUrl;
        }

        /**
         * @return The chapterUrlRaw
         */
        public String getChapterUrlRaw() {
            return chapterUrlRaw;
        }

        /**
         * @param chapterUrlRaw The chapter_url_raw
         */
        public void setChapterUrlRaw(String chapterUrlRaw) {
            this.chapterUrlRaw = chapterUrlRaw;
        }

        /**
         * @return The chapterUrl
         */
        public String getChapterUrl() {
            return chapterUrl;
        }

        /**
         * @param chapterUrl The chapter_url
         */
        public void setChapterUrl(String chapterUrl) {
            this.chapterUrl = chapterUrl;
        }
    }
}
