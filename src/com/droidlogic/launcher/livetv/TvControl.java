package com.droidlogic.launcher.livetv;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.tv.TvContentRating;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.droidlogic.app.DataProviderManager;
import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.tv.ChannelInfo;
import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.app.tv.TvDataBaseManager;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class TvControl {

    private static final String TAG = "TvControl";
    public static final String PROP_TV_PREVIEW = "vendor.tv.is.preview.window";
    public static final String DTVKIT_PACKAGE = "org.dtvkit.inputsource";

    public static String COMPONENT_TV_APP = "com.droidlogic.tvsource/com.droidlogic.tvsource.DroidLogicTv";
    public static String COMPONENT_LIVE_TV = "com.droidlogic.android.tv/com.android.tv.TvActivity";

    private static final String ACTION_OTP_INPUT_SOURCE_CHANGE = "droidlogic.tv.action.OTP_INPUT_SOURCE_CHANGED";

    private static final int INPUT_ID_LENGTH = 3;

    private static final int TV_MSG_PLAY_TV = 0;
    private static final int TV_MSG_BOOTUP_TO_TVAPP = 1;

    private TvViewManager mViewManager;
    private TvConfig mTvConfig;
    private Context mContext;

    private SystemControlManager mSystemControlManager = SystemControlManager.getInstance();
    private TvDataBaseManager mTvDataBaseManager;
    private TvInputManager mTvInputManager;

    private boolean isRadioChannel = false;
    private boolean isChannelBlocked = false;
    private boolean isAvNoSignal = false;

    private boolean mTvStartPlaying = false;

    private String mTvInputId;
    private Uri mChannelUri;
    private long mChannelId = -1;

    private boolean mActivityResumed;

    public TvControl(Context context, TvView mTvView, TextView prompt) {
        mContext = context;
        mViewManager = new TvViewManager(context, mTvView, prompt);
        mTvConfig = new TvConfig(context);
        mTvInputManager = (TvInputManager) mContext.getSystemService(Context.TV_INPUT_SERVICE);
        mTvDataBaseManager = new TvDataBaseManager(mContext);
        COMPONENT_TV_APP = COMPONENT_LIVE_TV;

        mViewManager.setCallback(new TvViewInputCallback());
    }

    public void setTvViewPosition(int left, int top, int right, int bottom, int transY, int duration) {
        mViewManager.setTvViewPosition(left, top, right, bottom, transY, duration);
    }

    public void play(long id) {
        mChannelId = id;
        mTvHandler.removeMessages(TV_MSG_PLAY_TV);
        mTvHandler.sendEmptyMessage(TV_MSG_PLAY_TV);
    }

    public void launchTvApp(long id) {
        mChannelId = id;
        mTvHandler.removeMessages(TV_MSG_BOOTUP_TO_TVAPP);
        mTvHandler.sendEmptyMessage(TV_MSG_BOOTUP_TO_TVAPP);
    }

    public void resume() {
        if (mTvConfig.checkNeedStartTvApp(true, mDelayedSourceChange != null)) {
            launchTvApp(-1);
            return;
        }

        if (mTvConfig.isTvFeture()) {
            stopMusicPlayer();
        }

        registerTvBroadcasts();

        if (mTvConfig.needPreviewFeture()) {
            //need to init channel when tv provider is ready
            mViewManager.enable(true);
            if (!mTvStartPlaying) {
                play(-1);
            }
        }

        mActivityResumed = true;
    }

    public void pause() {
        mActivityResumed = false;

        if (mTvConfig.needPreviewFeture()) {
            releasePlayingTv();
        }
    }

    public void stop() {
        pause();
        unregisterTvBroadcasts();
    }

    private boolean isTunerSource(String inputId) {
        TvInputInfo tvInputInfo = mTvInputManager.getTvInputInfo(inputId);
        return tvInputInfo != null && !tvInputInfo.isPassthroughInput();
    }

    private boolean isCurrentChannelBlocked() {
        return DataProviderManager.getBooleanValue(mContext, DroidLogicTvUtils.TV_CURRENT_BLOCK_STATUS, false);
    }

    private boolean isCurrentChannelBlockBlocked() {
        return DataProviderManager.getBooleanValue(mContext, DroidLogicTvUtils.TV_CURRENT_CHANNELBLOCK_STATUS, false);
    }

    public void setCurrentChannelBlocked(boolean blocked) {
        DataProviderManager.putBooleanValue(mContext, DroidLogicTvUtils.TV_CURRENT_BLOCK_STATUS, blocked);
    }

    public boolean initChannelWhenChannelReady() {
        boolean result = false;
        long channelId = DataProviderManager.getLongValue(mContext, DroidLogicTvUtils.TV_DTV_CHANNEL_INDEX, -1);
        //int deviceId = DataProviderManager.getIntValue(getContentResolver(), DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
        if (channelId != -1) {
            String inputid = "";
            Uri channelUri = TvContract.buildChannelUri(channelId);
            ChannelInfo currentChannel = mTvDataBaseManager.getChannelInfo(channelUri);
//            if (currentChannel != null && mChannelId != -1) {
//                inputid = currentChannel.getInputId();
//                String cur_inputid = DroidLogicTvUtils.getCurrentInputId(mContext);
//                if (inputid != null && !inputid.equals(cur_inputid)) {
//                    DroidLogicTvUtils.setCurrentInputId(mContext, inputid);
//                }
//            }

            inputid = DroidLogicTvUtils.getCurrentInputId(mContext);

            if (currentChannel != null && isTunerSource(inputid)
                    && currentChannel.isLocked() && mTvInputManager.isParentalControlsEnabled()) {
                isChannelBlocked = true;
            } else {
                isChannelBlocked = false;
            }
        } else {
            isChannelBlocked = false;
            String inputid = DroidLogicTvUtils.getCurrentInputId(mContext);
            if (isTunerSource(inputid)) {
                setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
                return false;
            }
        }
        Logger.d(TAG, "initChannelWhenChannelReady isChannelBlocked = " + isChannelBlocked + ", isCurrentChannelBlockBlocked = " + isCurrentChannelBlockBlocked());
        if (!isChannelBlocked || !isCurrentChannelBlockBlocked()) {
            result = true;
        } else {
            setTvPrompt(TvPrompt.TV_PROMPT_BLOCKED);
            mViewManager.setStreamVolume(0);
            result = false;
        }
        return result;
    }

    private boolean compareInputId(String inputId, TvInputInfo info) {
        Logger.d(TAG, "compareInputId currentInputId " + inputId + " info " + info);
        if (null == info) {
            Logger.d(TAG, "compareInputId info null");
            return false;
        }
        String infoInputId = info.getId();
        if (TextUtils.isEmpty(inputId) || TextUtils.isEmpty(infoInputId)) {
            Logger.d(TAG, "inputId empty");
            return false;
        }
        if (TextUtils.equals(inputId, infoInputId)) {
            return true;
        }

        String[] inputIdArr = inputId.split("/");
        String[] infoInputIdArr = infoInputId.split("/");
        // InputId is like com.droidlogic.tvinput/.services.Hdmi1InputService/HW5
        if (inputIdArr.length == INPUT_ID_LENGTH && infoInputIdArr.length == INPUT_ID_LENGTH) {
            // For hdmi device inputId could change to com.droidlogic.tvinput/.services.Hdmi2InputService/HDMI200008
            if (inputIdArr[0].equals(infoInputIdArr[0]) && inputIdArr[1].equals(infoInputIdArr[1])) {
                return true;
            }
        }
        return false;
    }

    private void setChannelUri(long channelId) {
        Uri channelUri = TvContract.buildChannelUri(channelId);
        ChannelInfo currentChannel = mTvDataBaseManager.getChannelInfo(channelUri);
        String currentSignalType = DroidLogicTvUtils.getCurrentSignalType(mContext) == DroidLogicTvUtils.SIGNAL_TYPE_ERROR
                ? TvContract.Channels.TYPE_ATSC_T : DroidLogicTvUtils.getCurrentSignalType(mContext);
        Logger.d(TAG, "channelid = " + channelId + "   [currentChannel] =" + currentChannel);
        if (currentChannel != null) {
            if (!TvContract.Channels.TYPE_OTHER.equals(currentChannel.getType())) {
                if (DroidLogicTvUtils.isAtscCountry(mContext)) {
                    if (currentChannel.getSignalType().equals(currentSignalType)) {
                        isRadioChannel = ChannelInfo.isRadioChannel(currentChannel);
                        mChannelUri = channelUri;
                        setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
                    }
                } else if (DroidLogicTvUtils.isATV(mContext) && currentChannel.isAnalogChannel()) {
                    isRadioChannel = ChannelInfo.isRadioChannel(currentChannel);
                    mChannelUri = channelUri;
                    setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
                } else if (DroidLogicTvUtils.isDTV(mContext) && currentChannel.isDigitalChannel()) {
                    isRadioChannel = ChannelInfo.isRadioChannel(currentChannel);
                    mChannelUri = channelUri;
                    setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
                } else {
                    if (TextUtils.equals(DroidLogicTvUtils.getSearchInputId(mContext), currentChannel.getInputId())) {
                        isRadioChannel = ChannelInfo.isRadioChannel(currentChannel);
                        mChannelUri = channelUri;
                        setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
                    }
                }
            } else {
                mChannelUri = TvContract.buildChannelUri(channelId);
            }
        } else {
            ArrayList<ChannelInfo> channelList = mTvDataBaseManager.getChannelList(mTvInputId, ChannelInfo.COMMON_PROJECTION, null, null);
            if (channelList != null && channelList.size() > 0) {
                for (int i = 0; i < channelList.size(); i++) {
                    ChannelInfo channel = channelList.get(i);
                    if (TvContract.Channels.TYPE_OTHER.equals(channel.getType())) {
                        if (TextUtils.equals(DroidLogicTvUtils.getSearchInputId(mContext), channel.getInputId())) {
                            mChannelUri = channel.getUri();
                            Logger.d(TAG, "current other type channel not exisit, find a new channel instead: " + mChannelUri);
                            return;
                        }
                    } else if (DroidLogicTvUtils.isAtscCountry(mContext)) {
                        if (channel.getSignalType().equals(currentSignalType)) {
                            mChannelUri = channel.getUri();
                            Logger.d(TAG, "current channel not exisit, find a new channel instead: " + mChannelUri);
                            return;
                        }
                    } else {
                        if (DroidLogicTvUtils.isATV(mContext) && channel.isAnalogChannel()) {
                            mChannelUri = channel.getUri();
                            Logger.d(TAG, "current channel not exisit, find a new channel instead: " + mChannelUri);
                            return;
                        } else if (DroidLogicTvUtils.isDTV(mContext) && channel.isDigitalChannel()) {
                            mChannelUri = channel.getUri();
                            Logger.d(TAG, "current channel not exisit, find a new channel instead: " + mChannelUri);
                            return;
                        }
                    }
                }
            } else {
                mChannelUri = TvContract.buildChannelUri(-1);
            }
        }
    }

    public void tuneTvView() {
        stopMusicPlayer();

        //float window don't need load PQ
        mSystemControlManager.setProperty(PROP_TV_PREVIEW, "true");

        mTvInputId = null;
        mChannelUri = null;

        setTvPrompt(TvPrompt.TV_PROMPT_TUNING/*TV_PROMPT_GOT_SIGNAL*/);

        int device_id;
        long channel_id;
        device_id = DataProviderManager.getIntValue(mContext, DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
        channel_id = DataProviderManager.getLongValue(mContext, DroidLogicTvUtils.TV_DTV_CHANNEL_INDEX, -1);
        isRadioChannel = DataProviderManager.getIntValue(mContext, DroidLogicTvUtils.TV_CURRENT_CHANNEL_IS_RADIO, 0) == 1 ? true : false;
        Logger.d(TAG, "TV get device_id=" + device_id + " dtv=" + channel_id);

        String inputid = DroidLogicTvUtils.getCurrentInputId(mContext);
        List<TvInputInfo> input_list = mTvInputManager.getTvInputList();
        Logger.d(TAG, "----input id:" + inputid);
        TvInputInfo currentInfo = null;
        for (TvInputInfo info : input_list) {
            /*if (parseDeviceId(info.getId()) == device_id) {
                mTvInputId = info.getId();
            }*/
            if (compareInputId(inputid, info)) {
                mTvInputId = info.getId();
                currentInfo = info;
                break;
            }
        }

        if (TextUtils.isEmpty(mTvInputId)) {
            Logger.i(TAG, "device" + device_id + " is not exist");
            setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
            return;
        } else {
            if (isTunerSource(inputid)) {
                setChannelUri(channel_id);
            } else {
                mChannelUri = TvContract.buildChannelUriForPassthroughInput(mTvInputId);
            }
        }

        Logger.d(TAG, "TV play tune inputId=" + mTvInputId + " uri=" + mChannelUri);
        if (mChannelUri != null && (DroidLogicTvUtils.getChannelId(mChannelUri) > 0
                || (currentInfo != null && currentInfo.isPassthroughInput()))) {
            mViewManager.tune(mTvInputId, mChannelUri);
        }

        if (mChannelUri != null && !TvContract.isChannelUriForPassthroughInput(mChannelUri)) {
            ChannelInfo current = mTvDataBaseManager.getChannelInfo(mChannelUri);
            if (current != null/* && (!mTvInputManager.isParentalControlsEnabled() ||
                        (mTvInputManager.isParentalControlsEnabled() && !current.isLocked()))*/) {
                if (isCurrentChannelBlocked() && !current.getInputId().startsWith(DTVKIT_PACKAGE)) {
                    Logger.d(TAG, "current channel is blocked");
                    setTvPrompt(TvPrompt.TV_PROMPT_BLOCKED);
                } else {
                    setTvPrompt(TvPrompt.TV_PROMPT_TUNING);
                    Logger.d(TAG, "TV play tune continue as no channel blocks");
                }
            } else {
                setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
                mViewManager.setStreamVolume(0);
                Logger.d(TAG, "TV play not tune as channel blocked");
            }
        } else if (mChannelUri == null) {
            Logger.d(TAG, "TV play not tune as mChannelUri null");
            setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
            mViewManager.setStreamVolume(0);
        }

        if (device_id == DroidLogicTvUtils.DEVICE_ID_SPDIF) {
            setTvPrompt(TvPrompt.TV_PROMPT_SPDIF);
        }

        mTvStartPlaying = true;
    }

    public void releasePlayingTv() {
        Logger.d(TAG, "releasePlayingTv");
        isChannelBlocked = false;
        if (mTvStartPlaying) {
            mViewManager.enable(false);
        }
        mTvHandler.removeMessages(TV_MSG_PLAY_TV);
        mTvStartPlaying = false;
    }

    public void startOtpSource(Intent intent) {
        Logger.d(TAG, "startOtpSource");
        if (mTvStartPlaying) {
            releasePlayingTv();
        }
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Logger.e(TAG, " can't start LiveTv:" + e);
        }
    }

    public void stopMusicPlayer() {
        Intent intent = new Intent();
        intent.setAction("com.android.music.pause");
        intent.putExtra("command", "stop");
        mContext.sendBroadcast(intent);
    }

    public void startTvApp() {
        try {
            if (mChannelId >= 0) {
                Uri channelUri = TvContract.buildChannelUri(mChannelId);
                Intent intent = new Intent(Intent.ACTION_VIEW, channelUri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent();
                intent.setComponent(ComponentName.unflattenFromString(COMPONENT_TV_APP));
                mContext.startActivity(intent);
            }

        } catch (ActivityNotFoundException e) {
            Logger.e(TAG, " can't start TvSettings:" + e);
        }
    }

    public void setTvPrompt(int mode) {
        String text = null;
        Drawable background = null;

        switch (mode) {
            case TvPrompt.TV_PROMPT_GOT_SIGNAL:
                break;
            case TvPrompt.TV_PROMPT_NO_SIGNAL:
                if (mTvInputId != null && mTvInputId.startsWith(DTVKIT_PACKAGE)) {
                    text = mContext.getResources().getString(R.string.str_no_signal);
                    background = mContext.getResources().getDrawable(R.drawable.black);
                }
                break;
            case TvPrompt.TV_PROMPT_IS_SCRAMBLED:
                text = mContext.getResources().getString(R.string.str_scrambeled);
                if (isRadioChannel) {
                    background = mContext.getResources().getDrawable(R.drawable.black);
                }
                break;
            case TvPrompt.TV_PROMPT_NO_DEVICE:
                background = mContext.getResources().getDrawable(R.drawable.hotplug_out);
                break;
            case TvPrompt.TV_PROMPT_SPDIF:
                background = mContext.getResources().getDrawable(R.drawable.spdifin);
                break;
            case TvPrompt.TV_PROMPT_BLOCKED:
                text = mContext.getResources().getString(R.string.str_blocked);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_NO_CHANNEL:
                text = mContext.getResources().getString(R.string.str_no_channel);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_RADIO:
                text = mContext.getResources().getString(R.string.str_audio_only);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_TUNING:
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
        }

        mViewManager.setTvPrompt(text, background);
    }

    public class TvViewInputCallback extends TvView.TvInputCallback {

        public void onEvent(String inputId, String eventType, Bundle eventArgs) {
            Logger.d(TAG, "====onEvent==inputId =" + inputId + ", ===eventType =" + eventType);
            if (eventType.equals(DroidLogicTvUtils.AV_SIG_SCRAMBLED)) {
                setTvPrompt(TvPrompt.TV_PROMPT_IS_SCRAMBLED);
            }
        }

        @Override
        public void onVideoAvailable(String inputId) {
            mViewManager.invalidate();
            int device_id = DataProviderManager.getIntValue(mContext, DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
            if (device_id == DroidLogicTvUtils.DEVICE_ID_AV1 || device_id == DroidLogicTvUtils.DEVICE_ID_AV2) {
                isAvNoSignal = false;
            }
            if (!isChannelBlocked || !isCurrentChannelBlockBlocked()) {
                setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
                if (inputId != null && inputId.startsWith(DTVKIT_PACKAGE)) {
                    mViewManager.setStreamVolume(1);
                }
            } else {
                setTvPrompt(TvPrompt.TV_PROMPT_BLOCKED);
                mViewManager.setStreamVolume(0);
            }

            Logger.d(TAG, "====onVideoAvailable==inputId =" + inputId);
        }

        @Override
        public void onConnectionFailed(String inputId) {
            Logger.d(TAG, "====onConnectionFailed==inputId =" + inputId);
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (TextUtils.isEmpty(mTvInputId))
                        return;
                    mViewManager.tune(mTvInputId, mChannelUri);
                }
            }).start();
        }

        @Override
        public void onVideoUnavailable(String inputId, int reason) {
            Logger.d(TAG, "====onVideoUnavailable==inputId =" + inputId + ", ===reason =" + reason);
            switch (reason) {
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN:
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING:
                case TvInputManager.VIDEO_UNAVAILABLE_REASON_BUFFERING:
                    break;
                default:
                    break;
            }
            int device_id = DataProviderManager.getIntValue(mContext, DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
            if (device_id == DroidLogicTvUtils.DEVICE_ID_SPDIF) {
                setTvPrompt(TvPrompt.TV_PROMPT_SPDIF);
            } else if (device_id == DroidLogicTvUtils.DEVICE_ID_AV1 || device_id == DroidLogicTvUtils.DEVICE_ID_AV2) {
                isAvNoSignal = true;
                setTvPrompt(TvPrompt.TV_PROMPT_NO_SIGNAL);
            } else if (reason != TvInputManager.VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY &&
                    reason != TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING) {
                if (!TextUtils.equals(mChannelUri.toString(), TvContract.buildChannelUri(-1).toString())) {
                    setTvPrompt(TvPrompt.TV_PROMPT_NO_SIGNAL);
                    if (inputId != null && inputId.startsWith(DTVKIT_PACKAGE)) {
                        mViewManager.setStreamVolume(0);
                    }
                }
            } else if (reason == TvInputManager.VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY) {
                if (inputId != null && inputId.startsWith(DTVKIT_PACKAGE)) {
                    setTvPrompt(TvPrompt.TV_PROMPT_RADIO);
                    if (inputId != null && inputId.startsWith(DTVKIT_PACKAGE)) {
                        mViewManager.setStreamVolume(1);
                    }
                }
            }
        }

        @Override
        public void onContentBlocked(String inputId, TvContentRating rating) {
            Logger.d(TAG, "====onContentBlocked");
            setCurrentChannelBlocked(true);
            int device_id = DataProviderManager.getIntValue(mContext, DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
            isChannelBlocked = true;
            if (isAvNoSignal && (device_id == DroidLogicTvUtils.DEVICE_ID_AV1 || device_id == DroidLogicTvUtils.DEVICE_ID_AV2)) {
                setTvPrompt(TvPrompt.TV_PROMPT_NO_SIGNAL);
            } else {
                setTvPrompt(TvPrompt.TV_PROMPT_BLOCKED);
            }
            mViewManager.setStreamVolume(0);
        }

        @Override
        public void onContentAllowed(String inputId) {
            Logger.d(TAG, "====onContentAllowed ");
            setCurrentChannelBlocked(false);
            int device_id = DataProviderManager.getIntValue(mContext, DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
            if (device_id == DroidLogicTvUtils.DEVICE_ID_AV1 || device_id == DroidLogicTvUtils.DEVICE_ID_AV2) {
                isAvNoSignal = false;
            }
            isChannelBlocked = false;
            setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
            mViewManager.setStreamVolume(1);
        }

        @Override
        public void onTracksChanged(String inputId, List<TvTrackInfo> tracks) {
            Logger.d(TAG, "onTracksChanged inputId = " + inputId);
            //appyPrimaryAudioLanguage(tracks);
        }

        @Override
        public void onTrackSelected(String inputId, int type, String trackId) {
            Logger.d(TAG, "onTrackSelected inputId = " + inputId + ", type = " + type + ", trackId = " + trackId);
        }
    }

    private Intent mDelayedSourceChange;
    private BroadcastReceiver otherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Logger.d(TAG, " receive " + action);
            if (ACTION_OTP_INPUT_SOURCE_CHANGE.equals(action)) {
                Intent i = new Intent(TvInputManager.ACTION_SETUP_INPUTS);
                i.putExtra("from_cec_otp", true);
                i.putExtra(TvInputInfo.EXTRA_INPUT_ID, intent.getStringExtra(TvInputInfo.EXTRA_INPUT_ID));
                if (!mTvConfig.isBootvideoStopped()) {
                    mDelayedSourceChange = i;
                    if (mActivityResumed) {
                        mTvHandler.sendEmptyMessage(TV_MSG_BOOTUP_TO_TVAPP);
                    }
                } else if (mActivityResumed) {
                    Toast.makeText(mContext, R.string.toast_otp_input_change, Toast.LENGTH_LONG).show();
                    startOtpSource(i);
                } else {
                    Logger.d(TAG, " acitivity not resumed or bootvideo not finished, drop " + ACTION_OTP_INPUT_SOURCE_CHANGE);
                }
            }
        }
    };


    private boolean mBroadcastsRegistered = false;

    private void registerTvBroadcasts() {
        if (mBroadcastsRegistered) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OTP_INPUT_SOURCE_CHANGE);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        mContext.registerReceiver(otherReceiver, filter);
        mBroadcastsRegistered = true;
    }

    private void unregisterTvBroadcasts() {
        if (!mBroadcastsRegistered) {
            return;
        }
        mContext.unregisterReceiver(otherReceiver);
        mBroadcastsRegistered = false;
    }

    @SuppressLint("HandlerLeak")
    private final Handler mTvHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TV_MSG_PLAY_TV:
                    if (mTvConfig.isBootvideoStopped()) {
                        Logger.d(TAG, "bootvideo is stopped, and tvapp released, start tv play");
                        if (initChannelWhenChannelReady()) {
                            tuneTvView();
                        } else {
                            Logger.d(TAG, "screen blocked and no need start tv play");
                        }
                    } else {
                        //Loggerd(TAG, "bootvideo is not stopped, or tvapp not released, wait it");
                        mTvHandler.removeMessages(TV_MSG_PLAY_TV);
                        mTvHandler.sendEmptyMessageDelayed(TV_MSG_PLAY_TV, 200);
                    }
                    break;

                case TV_MSG_BOOTUP_TO_TVAPP:
                    if (mTvConfig.isBootvideoStopped()) {
                        Logger.d(TAG, "bootvideo is stopped, start tv app");
                        if (mDelayedSourceChange != null) {
                            startOtpSource(mDelayedSourceChange);
                            mDelayedSourceChange = null;
                            Toast.makeText(mContext, R.string.toast_otp_input_change, Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            startTvApp();
                        }
                    } else {
                        //Loggerd(TAG, "bootvideo is not stopped, wait it");
                        mTvHandler.removeMessages(TV_MSG_BOOTUP_TO_TVAPP);
                        mTvHandler.sendEmptyMessageDelayed(TV_MSG_BOOTUP_TO_TVAPP, 200);
                    }
                    break;
                default:
                    break;
            }
        }
    };

}
