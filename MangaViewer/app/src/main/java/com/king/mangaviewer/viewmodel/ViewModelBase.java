package com.king.mangaviewer.viewmodel;

import android.content.Context;

public class ViewModelBase {
    protected Context mContext;

    public ViewModelBase(){}
    public ViewModelBase(Context context){
        this.mContext = context;
    }
}
