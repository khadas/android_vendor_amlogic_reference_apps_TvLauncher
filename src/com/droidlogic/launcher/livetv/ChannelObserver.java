package com.droidlogic.launcher.livetv;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.droidlogic.launcher.util.Logger;

public class ChannelObserver extends ContentObserver {
    private static final String TAG = "ChannelObserver";

    public boolean mChannelChanged = false;

    public ChannelObserver() {
        super(new Handler());
    }

    public void reset(){
        mChannelChanged = false;
    }

    public boolean changed(){
        return mChannelChanged;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Logger.d(TAG, "detect channel changed =" + uri);
        mChannelChanged = true;
            /*if (DroidLogicTvUtils.matchsWhich(mChannelUri) == DroidLogicTvUtils.NO_MATCH) {
                ChannelInfo changedChannel = mTvDataBaseManager.getChannelInfo(uri);
                if (TextUtils.equals(changedChannel.getInputId(), mTvInputId)) {
                    Loggerd(TAG, "current channel is null, so tune to a new channel");
                    mChannelUri = uri;
                    tvView.tune(mTvInputId, mChannelUri);
                }
            }*/
    }
}
