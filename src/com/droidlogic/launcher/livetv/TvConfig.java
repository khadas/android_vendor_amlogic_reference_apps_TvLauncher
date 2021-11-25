package com.droidlogic.launcher.livetv;

import android.content.ContentProviderClient;
import android.content.Context;
import android.text.TextUtils;

import android.media.tv.TvContract;
import android.os.SystemProperties;


import com.droidlogic.app.DataProviderManager;
import com.droidlogic.app.DroidLogicUtils;
import com.droidlogic.app.SystemControlManager;

public class TvConfig {
    public static final String TV_START_UP_ENTER_APP = "tv_start_up_enter_app";
    private static final String TAG = "TvConfig";

    private Context mContext;
    private SystemControlManager mSystemControlManager = SystemControlManager.getInstance();

    public TvConfig(Context context){
        mContext = context;
    }

    public boolean isMboxFeture () {
        return mSystemControlManager.getPropertyBoolean("ro.vendor.platform.has.mbxuimode", false);
    }

    public boolean isTvFeture () {
        return TextUtils.equals(mSystemControlManager.getPropertyString("ro.vendor.platform.is.tv", ""), "1");
    }

    public boolean needPreviewFeture () {
        return isTvFeture() && mSystemControlManager.getPropertyBoolean("vendor.tv.need.droidlogic.preview_window", false);
    }

    public boolean isBootvideoStopped() {
        ContentProviderClient tvProvider = mContext.getContentResolver().acquireContentProviderClient(TvContract.AUTHORITY);

        int bootvideo     = SystemProperties.getInt("persist.vendor.media.bootvideo", 50);
        String videoexit  = SystemProperties.get("service.bootvideo.exit", "1");

        return (tvProvider != null) &&
                (((bootvideo  > 100) && TextUtils.equals(videoexit, "0"))
                        || (bootvideo  <= 100));
    }


    public boolean checkNeedStartTvApp(boolean close,boolean delayedSourceChange) {
        boolean ret = false;
        if ((DroidLogicUtils.isTv() &&
                !TextUtils.equals(mSystemControlManager.getProperty("vendor.tv.launcher.firsttime.launch"), "false") &&
                DataProviderManager.getIntValue(mContext, TV_START_UP_ENTER_APP, 0) > 0)
                || delayedSourceChange
                ) {
            ret = true;
        }

        if (close) {
            mSystemControlManager.setProperty("vendor.tv.launcher.firsttime.launch", "false");
        }

        return ret;
    }

}
