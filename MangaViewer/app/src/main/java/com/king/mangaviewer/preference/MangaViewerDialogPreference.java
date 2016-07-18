package com.king.mangaviewer.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by KinG on 8/22/2015.
 */
public class MangaViewerDialogPreference extends DialogPreference {

    private OnDialogClickListener mOnDialogClickListener;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MangaViewerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MangaViewerDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MangaViewerDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MangaViewerDialogPreference(Context context) {
        super(context);
    }
    @Override
    protected void onClick() {
        new AlertDialog.Builder(this.getContext())
                .setTitle(this.getDialogTitle())
                .setMessage(this.getDialogMessage())
                .setPositiveButton(this.getPositiveButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mOnDialogClickListener != null) {
                            mOnDialogClickListener.onClick();
                        }
                    }
                })
                .setNegativeButton(this.getNegativeButtonText(),null)
                .show();
    }

    public void setOnDialogClickListener(OnDialogClickListener l)
    {
        this.mOnDialogClickListener = l;
    }
    public interface OnDialogClickListener{
        void onClick();
    }
}
