package com.droidlogic.launcher.livetv;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.tv.ChannelInfo;
import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.util.Logger;

import java.util.List;

public class TvControl {

    private static final String TAG = "TvControl";
    public static final String PROP_TV_PREVIEW  = "vendor.tv.is.preview.window";
    public static final String DTVKIT_PACKAGE   = "com.droidlogic.dtvkit.inputsource";

    public static String COMPONENT_TV_APP       = "com.droidlogic.tvsource/com.droidlogic.tvsource.DroidLogicTv";
    public static String COMPONENT_LIVE_TV      = "com.droidlogic.android.tv/com.android.tv.TvActivity";

    private static final String ACTION_OTP_INPUT_SOURCE_CHANGE = "droidlogic.tv.action.OTP_INPUT_SOURCE_CHANGED";
    private static final String EVENT_CHANNEL_LIST_UPDATE      = "event_channel_list_updated";

    public static final String EVENT_SIGNAL_SCRAMBLED_SERVICE   = "signal_scrambled_service";
    public static final String EVENT_SIGNAL_INVALID_SERVICE     = "signal_invalid_service";
    public static final String EVENT_SIGNAL_DATA_SERVICE        = "signal_data_service";

    //ci plus
    private static final String ACTION_CI_PLUS_INFO = "ci_plus_info";
    public static final String CI_PLUS_COMMAND = "command";
    public static final String VALUE_CI_PLUS_COMMAND_SEARCH_REQUEST = "search_request";
    public static final String VALUE_CI_PLUS_COMMAND_SEARCH_FINISHED = "search_finished";
    public static final String VALUE_CI_PLUS_COMMAND_HOST_CONTROL = "host_control";
    public static final String VALUE_CI_PLUS_SEARCH_MODULE = "search_module";
    public static final String VALUE_CI_PLUS_SEARCH_RESULT_IS_OPERATOR_PROFILE_SUPPORTED = "is_operator_profile_supported";
    public static final String VALUE_CI_PLUS_CHANNEL = "host_control_channel";
    public static final String VALUE_CI_PLUS_TUNE_QUIETLY = "tune_quietly";

    private static final String DROIDLOGIC_LAUNCHER_FOREGROUND =
        "droidlogic_launcher_foreground";

    private static final int TV_MSG_PLAY_TV         = 0;
    private static final int TV_MSG_BOOTUP_TO_TVAPP = 1;

    private TvViewManager   mViewManager;
    private TvConfig        mTvConfig;
    private Context         mContext;

    private SystemControlManager mSystemControlManager = SystemControlManager.getInstance();

    private TvInputManager      mTvInputManager;
    private ChannelDataManager  mChannelDataManager;

    private boolean isRadioChannel      = false;
    private boolean isChannelBlocked    = false;
    private boolean isAvNoSignal        = false;
    private boolean mTvStartPlaying     = false;

    private String  mTvInputId;
    private Uri     mChannelUri;
    private long    mPlayChannelId = -1;
    private String  mPlayInputId;

    private long    mCiTuneChannelId = -1;

    private boolean mActivityResumed;
    private boolean mBootComplete      = false;
    private boolean mVideoAvailable    = false;

    public TvControl(Context context, TvView mTvView, TextView prompt) {
        mContext            = context;
        mViewManager        = new TvViewManager(context, mTvView, prompt);
        mTvConfig           = new TvConfig(context);
        mTvInputManager     = (TvInputManager) mContext.getSystemService(Context.TV_INPUT_SERVICE);
		mChannelDataManager = new ChannelDataManager(context);
        COMPONENT_TV_APP    = COMPONENT_LIVE_TV;

        mViewManager.setCallback(new TvViewInputCallback());
    }

    public void setTvViewPosition(int left, int top, int right, int bottom, int transY, int duration) {
        mViewManager.setTvViewPosition(left, top, right, bottom, transY, duration);
    }

    /*
     * play a channel in small window
     * */
    public void play(long id) {
        if (mTvConfig.needPreviewFeature()) {
            mPlayInputId = getCurrentInputSourceId();
            mPlayChannelId = id;
            mTvHandler.removeMessages(TV_MSG_PLAY_TV);
            mTvHandler.sendEmptyMessage(TV_MSG_PLAY_TV);
        }
    }

    /*
    * play a source input channel in small window
    * */
    public void play(String inputId) {
        if (mTvConfig.needPreviewFeature()) {
            mPlayInputId = inputId;
            mPlayChannelId = -1;
            mTvHandler.removeMessages(TV_MSG_PLAY_TV);
            mTvHandler.sendEmptyMessage(TV_MSG_PLAY_TV);
        }
    }

    public void launchTvApp(long id) {
        mPlayChannelId = id;
        mTvHandler.removeMessages(TV_MSG_BOOTUP_TO_TVAPP);
        mTvHandler.sendEmptyMessage(TV_MSG_BOOTUP_TO_TVAPP);
    }

    //boot to tvapp when power on
    public boolean checkNeedStartTvApp() {
        return mTvConfig.checkNeedStartTvApp(false, false);
    }

    public void resume() {
        setForegroundState(true);
        if (mTvConfig.checkNeedStartTvApp(true, mDelayedSourceChange != null)) {
            launchTvApp(-1);
            return;
        }

        if (mTvConfig.isTvFeature()) {
            stopMusicPlayer();
        }

        registerTvBroadcasts();

        if (mTvConfig.needPreviewFeature()) {
            //need to init channel when tv provider is ready
            mViewManager.enable(true);
            if (!mTvStartPlaying) {
                play(-1);
            }
        }

        mActivityResumed = true;
    }

    public void pause() {
        setForegroundState(false);
        mActivityResumed = false;
        if (mTvConfig.needPreviewFeature()) {
            releasePlayingTv();
        }
    }

    public void stop() {
        pause();
        unregisterTvBroadcasts();
    }

    private boolean isTunerSource(String inputId) {
        TvInputInfo info = getTvInputInfo(inputId);
        if (info != null)
            return !info.isPassthroughInput();

        return false;
    }

    private TvInputInfo getTvInputInfo(String inputId) {
        List<TvInputInfo> infos = mTvInputManager.getTvInputList();
        for(TvInputInfo info : infos){
            if(info.getId().equals(inputId))
                return info;
        }

        return null;
    }


    private boolean isCurrentChannelBlocked() {
        return mChannelDataManager.isCurrentChannelBlocked();
    }

    private boolean isCurrentChannelBlockBlocked() {
        return mChannelDataManager.isCurrentChannelBlockBlocked();
    }

    public void setCurrentChannelBlocked(boolean blocked) {
        mChannelDataManager.setCurrentChannelBlocked(blocked);
    }

    public boolean isChannelReady(String inputid, long channelId) {
        boolean result = false;

        isChannelBlocked = false;
        if (isTunerSource(inputid)){
            if (channelId == -1) {
                channelId = getPlayChannelId(inputid);
            }
            if (channelId == -1) {
                setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
                return false;
            }
            mPlayChannelId = channelId;
            Uri channelUri = TvContract.buildChannelUri(channelId);
            //if (mChannelDataManager.isChennelLocked(channelUri) && mTvInputManager.isParentalControlsEnabled()) {
            if (mChannelDataManager.isChennelLocked(channelUri)) {
                isChannelBlocked = true;
            }
        }
        else{
            isChannelBlocked = false;
        }

        //if (!isChannelBlocked || !isCurrentChannelBlockBlocked()) {
        if (!isChannelBlocked) {
            result = true;
        } else {
            setTvPrompt(TvPrompt.TV_PROMPT_BLOCKED);
            mViewManager.setStreamVolume(0);
            result = false;
        }
        return result;
    }

    private void setChannelUri(boolean isPassthroughInput, long channelId) {
        if (!isPassthroughInput) {
            mChannelUri = TvContract.buildChannelUri(channelId);
            if (channelId != -1) {
                isRadioChannel = mChannelDataManager.isRadioChannel(mChannelUri);
                setTvPrompt(TvPrompt.TV_PROMPT_GOT_SIGNAL);
            }
        } else {
            mChannelUri = TvContract.buildChannelUriForPassthroughInput(mTvInputId);
        }
    }

    public void tuneTvView(String inputId, long channelId) {
        isRadioChannel = false;
        mTvInputId = null;
        mChannelUri = null;

        stopMusicPlayer();
        //float window don't need load PQ
        mSystemControlManager.setProperty(PROP_TV_PREVIEW, "true");
        setTvPrompt(TvPrompt.TV_PROMPT_TUNING);

        TvInputInfo currentInputInfo = getInputSourceInfo(inputId);
        if (TextUtils.isEmpty(inputId) || currentInputInfo == null) {
            Logger.i(TAG, "input " + inputId + " is not exist");
            setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
            return;
        }

        mTvInputId = inputId;
        boolean isPassthroughInput = currentInputInfo.isPassthroughInput();

        setChannelUri(isPassthroughInput, channelId);
        ChannelInfo currentChannel = mChannelDataManager.getChannelInfo(mChannelUri);
        Logger.d(TAG, "TV play tune inputId=" + inputId + " uri=" + mChannelUri);
        if (!isPassthroughInput) {
            if (mChannelUri == null || currentChannel == null) {
                Logger.d(TAG, "TV play not tune as mChannelUri null");
                setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
                mViewManager.setStreamVolume(0);
                return;
            }
        }

        if ((mChannelDataManager.getChannelId(mChannelUri) > 0
                && currentChannel.getSetHidden() == 0
                && currentChannel.getSetDelete() == 0) || isPassthroughInput) {
            mViewManager.enable(true);
            mViewManager.tune(inputId, mChannelUri);
        }

        if (!isPassthroughInput) {
            if (isCurrentChannelBlocked() && !inputId.startsWith(DTVKIT_PACKAGE)) {
                Logger.d(TAG, "current channel is blocked");
                setTvPrompt(TvPrompt.TV_PROMPT_BLOCKED);
            } else if (currentChannel.getSetDelete() == 1) {
                Logger.d(TAG, "current channel is delete");
                setTvPrompt(TvPrompt.TV_PROMPT_NO_CHANNEL);
            } else if (currentChannel.getSetHidden() == 1) {
                Logger.d(TAG, "current channel is hide");
                setTvPrompt(TvPrompt.TV_PROMPT_IS_SKIP);
            } else {
                setTvPrompt(TvPrompt.TV_PROMPT_TUNING);
                Logger.d(TAG, "TV play tune continue");
            }
        }
        else{
            if (isSpdifDevice(inputId)) {
                setTvPrompt(TvPrompt.TV_PROMPT_SPDIF);
            }
        }

        mTvStartPlaying = true;
    }

    public void releasePlayingTv() {
        //Logger.d(TAG, "releasePlayingTv");
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

    /**
     * Whether the pip window of launcher is enabled.
     *
     */
    private void setForegroundState(boolean foreground) {
        Global.putInt(mContext.getContentResolver(),
            DROIDLOGIC_LAUNCHER_FOREGROUND, foreground ? 1 : 0);
    }

    public void stopMusicPlayer() {
        Intent intent = new Intent();
        intent.setAction("com.android.music.pause");
        intent.putExtra("command", "stop");
        mContext.sendBroadcast(intent);
    }

    public void startTvApp() {
        try {
            if (mPlayChannelId >= 0) {
                Uri channelUri = TvContract.buildChannelUri(mPlayChannelId);
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
        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        switch (mode) {
            case TvPrompt.TV_PROMPT_GOT_SIGNAL:
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                text = mContext.getResources().getString(R.string.str_audio_only);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_TUNING:
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_DATA_SERVICE:
                text = mContext.getResources().getString(R.string.str_data_service);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_IS_SKIP:
                text = mContext.getResources().getString(R.string.str_channel_skip);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_PROMPT_IS_DELETE:
                text = mContext.getResources().getString(R.string.str_channel_delete);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
            case TvPrompt.TV_INVALID_SERVICE:
                text = mContext.getResources().getString(R.string.str_invalid_service);
                background = mContext.getResources().getDrawable(R.drawable.black);
                break;
        }

        mViewManager.setTvPrompt(text, background);
    }

    public class TvViewInputCallback extends TvView.TvInputCallback {

        public void onEvent(String inputId, String eventType, Bundle eventArgs) {
            Logger.d(TAG, "====onEvent==inputId =" + inputId + ", ===eventType =" + eventType);
            if (eventType.equals(EVENT_SIGNAL_SCRAMBLED_SERVICE)) {
                setTvPrompt(TvPrompt.TV_PROMPT_IS_SCRAMBLED);
            }
            else if (eventType.equals(EVENT_SIGNAL_DATA_SERVICE)) {
                setTvPrompt(TvPrompt.TV_PROMPT_DATA_SERVICE);
            }
            else if (eventType.equals(EVENT_SIGNAL_INVALID_SERVICE)) {
                setTvPrompt(TvPrompt.TV_INVALID_SERVICE);
            }
            else if (eventType.equals(EVENT_CHANNEL_LIST_UPDATE)) {
                replay(true);
            }
            else if (eventType.equals(ACTION_CI_PLUS_INFO)) {
                if (eventArgs != null) {
                    String command = eventArgs.getString(CI_PLUS_COMMAND, null);
                    if (TextUtils.isEmpty(command)) {
                        return;
                    }
                    if (command.equals(VALUE_CI_PLUS_COMMAND_SEARCH_REQUEST)) {
                        //A pop-up box is required to allow the user to choose whether to search for channels
                        int search_module = eventArgs.getInt(VALUE_CI_PLUS_SEARCH_MODULE);
                        openSearchRequestByCI(search_module);
                    } else if (command.equals(VALUE_CI_PLUS_COMMAND_SEARCH_FINISHED)) {
                        boolean isSupportOperatorProfile = eventArgs.getBoolean(VALUE_CI_PLUS_SEARCH_RESULT_IS_OPERATOR_PROFILE_SUPPORTED);
                        showSearchResultByCI(isSupportOperatorProfile);
                    } else if (command.equals(VALUE_CI_PLUS_COMMAND_HOST_CONTROL)) {
                        long host_control_channel = eventArgs.getLong(VALUE_CI_PLUS_CHANNEL);
                        if (host_control_channel != -1) {
                            mCiTuneChannelId = host_control_channel;
                        }
                    }
                }
            }
        }

        @Override
        public void onVideoAvailable(String inputId) {
            mViewManager.invalidate();
            mVideoAvailable = true;
            if (isAVDevice(inputId)) {
                isAvNoSignal = false;
            }
            if (!isChannelBlocked || !isCurrentChannelBlocked()) {
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

            if (isSpdifDevice(inputId)) {
                setTvPrompt(TvPrompt.TV_PROMPT_SPDIF);
            } else if (isAVDevice(inputId)) {
                isAvNoSignal = true;
                setTvPrompt(TvPrompt.TV_PROMPT_NO_SIGNAL);
            } else if (reason != TvInputManager.VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY &&
                    reason != TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING) {
                if (!TextUtils.equals(mChannelUri.toString(), TvContract.buildChannelUri(-1).toString())) {
                    if (mChannelDataManager.isDataChannel(mChannelUri)) {
                        setTvPrompt(TvPrompt.TV_PROMPT_DATA_SERVICE);
                    } else {
                        setTvPrompt(TvPrompt.TV_PROMPT_NO_SIGNAL);
                    }
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

            isChannelBlocked = true;
            if (isAvNoSignal && isAVDevice(inputId)) {
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

            if (isAVDevice(inputId)) {
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
            else if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                mBootComplete = true;
                replay(false);
            }
        }
    };


    private boolean mBroadcastsRegistered = false;

    private void registerTvBroadcasts() {
        if (mBroadcastsRegistered) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_OTP_INPUT_SOURCE_CHANGE);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        mContext.registerReceiver(otherReceiver, filter, 2/*Context.RECEIVER_EXPORTED*/);
        mBroadcastsRegistered = true;
    }

    private void unregisterTvBroadcasts() {
        if (!mBroadcastsRegistered) {
            return;
        }
        mContext.unregisterReceiver(otherReceiver);
        mBroadcastsRegistered = false;
    }

    private void replay(boolean force) {
        if ((mBootComplete && !mVideoAvailable) || force) {
            Logger.d(TAG, "replay...");
            mBootComplete  = false;
            play(-1);
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mTvHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TV_MSG_PLAY_TV:
                    if (mTvConfig.isBootvideoStopped()) {
                        Logger.d(TAG, "bootvideo is stopped, and tvapp released, start tv play");
                        if (isChannelReady(mPlayInputId, mPlayChannelId)) {
                            tuneTvView(mPlayInputId, mPlayChannelId);
                        } else {
                            Logger.d(TAG, "screen blocked and no need start tv play");
                        }
                    } else {
                        //Logger.d(TAG, "bootvideo is not stopped, or tvapp not released, wait it");
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
                        Logger.d(TAG, "bootvideo is not stopped, wait it");
                        mTvHandler.removeMessages(TV_MSG_BOOTUP_TO_TVAPP);
                        mTvHandler.sendEmptyMessageDelayed(TV_MSG_BOOTUP_TO_TVAPP, 50);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private long getPlayChannelId(String inputId){
        long channelId = mChannelDataManager.getLastPlayChannelForInput(inputId);
//        if (channelId == -1) {
//            channelId = mChannelDataManager.getLastPlayChannel(inputId);
//        }
        return channelId;
    }

    private String getCurrentInputSourceId() {
        return DroidLogicTvUtils.getCurrentInputId(mContext);
    }

    private TvInputInfo getInputSourceInfo(String id) {
        List<TvInputInfo> input_list = mTvInputManager.getTvInputList();
        if (input_list == null) {
            return null;
        }

        for (TvInputInfo tvInput : input_list) {
            if (tvInput.getId().equals(id)) {
                return tvInput;
            }
        }
        return null;
    }

    private boolean isSpdifDevice(String id) {
        TvInputInfo input = getInputSourceInfo(id);
        if (input == null) {
            return false;
        }

        int deviceId = DroidLogicTvUtils.getHardwareDeviceId(input);
        return (deviceId == DroidLogicTvUtils.DEVICE_ID_SPDIF);
    }

    private boolean isAVDevice(String id) {
        TvInputInfo input = getInputSourceInfo(id);
        if (input == null) {
            return false;
        }

        int deviceId = DroidLogicTvUtils.getHardwareDeviceId(input);
        return (deviceId == DroidLogicTvUtils.DEVICE_ID_AV1 || deviceId == DroidLogicTvUtils.DEVICE_ID_AV2);
    }

    private void openSearchRequestByCI(int searchModule) {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.ci_message_title))
                .setMessage(mContext.getString(R.string.ci_scan_request_message))
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                Bundle bundle = new Bundle();
                                bundle.putString(CI_PLUS_COMMAND,VALUE_CI_PLUS_COMMAND_SEARCH_REQUEST);
                                bundle.putInt(VALUE_CI_PLUS_SEARCH_MODULE, searchModule);
                                mViewManager.sendAppPrivateCommand(ACTION_CI_PLUS_INFO, bundle);
                            }
                        })
                .setNegativeButton(mContext.getString(R.string.cancel), null)
                .show();
    }

    private void showSearchResultByCI(boolean isSupportOperatorProfile) {
        String ciTuneChannelDisplayName = "";
        if (isSupportOperatorProfile && mCiTuneChannelId != -1) {
            Uri channelUri = TvContract.buildChannelUri(mCiTuneChannelId);
            ChannelInfo info = mChannelDataManager.getChannelInfo(channelUri); //check if channel exist
            if (info != null) {
                ciTuneChannelDisplayName = info.getDisplayName();
            }
        }
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.ci_message_title))
                .setMessage((isSupportOperatorProfile && !TextUtils.isEmpty(ciTuneChannelDisplayName))
                        ? mContext.getString(R.string.ci_scan_operator_profile_message) + " " + ciTuneChannelDisplayName + "?"
                        : mContext.getString(R.string.ci_scan_result_no_channel_message))
                .setPositiveButton(
                        android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialogInterface, int i) {
                                if (isSupportOperatorProfile) {
                                    mPlayChannelId = mCiTuneChannelId;
                                    mChannelDataManager.putLongValue(mContext, mPlayInputId, mCiTuneChannelId);
                                } else {
                                    play(-1);
                                }
                            }
                        })
                .setNegativeButton(
                        mContext.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (isSupportOperatorProfile) {
                                    //The Dtvkit background has switched channels, returning to the previously played channel
                                    play(-1);
                                }
                            }
                        })
                .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if (isSupportOperatorProfile) {
                                    play(-1);
                                }
                            }
                            return false;
                        }
                        })
                .show();
    }

}
