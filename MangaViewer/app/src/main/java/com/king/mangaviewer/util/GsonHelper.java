package com.king.mangaviewer.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Created by king on 2017-08-13.
 */

public class GsonHelper {
    private static Gson gson = new Gson();

    public static String toJson(Object obj){
        return gson.toJson(obj);
    }

    public static <T>T fromJson(String json, Type type){
        return gson.fromJson(json,type);
    }
}