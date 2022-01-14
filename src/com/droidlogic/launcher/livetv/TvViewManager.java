package com.droidlogic.launcher.livetv;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

public class TvViewManager {

    private final String TAG = "TvViewManager";

    public static final int TV_MODE_NORMAL = 0;
    public static final int TV_MODE_TOP = 1;
    public static final int TV_MODE_BOTTOM = 2;

    private static final int TV_WINDOW_WIDTH = 480;
    private static final int TV_WINDOW_HEIGHT = 320;

    private static final int TV_WINDOW_NORMAL_LEFT = 0;
    private static final int TV_WINDOW_NORMAL_TOP = 0;

    private static final int TV_WINDOW_RIGHT_LEFT = 1280 - TV_WINDOW_WIDTH;
    private static final int TV_WINDOW_BOTTOM_TOP = 720 - TV_WINDOW_HEIGHT;

    private Context mContext;
    private TvView mTvView;
    private int mTvViewMode;
    private TvPrompt mTvPrompt;
    private Rect mRect;

    public TvViewManager(Context context, TvView mTvView, TextView prompt) {
        mContext = context;
        mTvPrompt = new TvPrompt(prompt);
        this.mTvView = mTvView;
        if (mTvView != null) {
            mTvView.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTvView.setZOrderMediaOverlay(false);
            }
        }
    }

    public void setCallback(TvView.TvInputCallback callback) {
        if (mTvView != null) {
            mTvView.setCallback(callback);
        }
    }

    private int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setTvViewPosition(int mode) {

        int left = -1;
        int top = -1;
        int right = -1;
        int bottom = -1;
        int transY = 0;
        int duration = 0;

        mTvViewMode = mode;
        switch (mode) {
            case TV_MODE_TOP:
                left = dipToPx(mContext, TV_WINDOW_RIGHT_LEFT);
                top = dipToPx(mContext, TV_WINDOW_NORMAL_TOP);
                right = left + dipToPx(mContext, TV_WINDOW_WIDTH);
                bottom = top + dipToPx(mContext, TV_WINDOW_HEIGHT);
                duration = 500;
                break;
            case TV_MODE_BOTTOM:
                left = dipToPx(mContext, TV_WINDOW_RIGHT_LEFT);
                top = dipToPx(mContext, TV_WINDOW_BOTTOM_TOP);
                right = left + dipToPx(mContext, TV_WINDOW_WIDTH);
                bottom = top + dipToPx(mContext, TV_WINDOW_HEIGHT);
                duration = 500;
                break;
            case TV_MODE_NORMAL:
            default:
                left = dipToPx(mContext, TV_WINDOW_NORMAL_LEFT);
                top = dipToPx(mContext, TV_WINDOW_NORMAL_TOP);
                right = left + dipToPx(mContext, TV_WINDOW_WIDTH);
                bottom = top + dipToPx(mContext, TV_WINDOW_HEIGHT);
                duration = 0;
                break;
        }
        setTvViewPosition(left, top, right, bottom, transY, duration);
    }

    public void setTvViewPosition(int left, int top, int right, int bottom, int transY, int duration) {
        mRect = new Rect(left, top, right, bottom);
        if (mTvView != null) {
            setViewPosition(mTvView, mRect);
            mTvView.animate()
                    .translationY(transY)
                    .setDuration(duration)
                    .start();
        }
        //mTvPrompt.setPosition(mRect);
    }

    public void setViewPosition(View view, Rect rect) {
        android.widget.FrameLayout.LayoutParams para;
        para = new android.widget.FrameLayout.LayoutParams(rect.width(), rect.height());
        para.setMargins(rect.left, rect.top, 0, 0);
        view.setLayoutParams(para);
    }

    public void setTvPrompt(String text, Drawable background) {
        mTvPrompt.setTvPrompt(text, background);
    }

    public void setStreamVolume(int vol) {
        if (mTvView != null) {
            mTvView.setStreamVolume(vol);
        }
    }

    public void tune(String inputId, Uri channelUri) {
        if (mTvView != null) {
            mTvView.tune(inputId, channelUri);
        }
    }

    public void invalidate() {
        if (mTvView != null) {
            mTvView.invalidate();
        }
    }

    public void enable(boolean enable) {
        if (mTvView != null) {
            if (enable) {
                mTvView.setVisibility(View.VISIBLE);
            } else {
                mTvView.setVisibility(View.GONE);
                mTvView.reset();
            }
        }
    }
}
