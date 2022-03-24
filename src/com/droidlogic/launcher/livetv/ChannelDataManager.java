package com.droidlogic.launcher.livetv;

import android.content.Context;
import android.media.tv.TvContract;
import android.net.Uri;
import android.text.TextUtils;

import com.droidlogic.app.DataProviderManager;
import com.droidlogic.app.tv.ChannelInfo;
import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.app.tv.TvDataBaseManager;

import java.util.ArrayList;

public class ChannelDataManager {
    public static final String TV_DTVKIT_SYSTEM = "tv_dtvkit_system";
    public static final String TV_DROIDLOGIC_PACKAGE = "com.droidlogic.tvinput";
    public static final String DTVKIT_PACKAGE = "com.droidlogic.dtvkit.inputsource";

    private Context mContext;
    private TvDataBaseManager mTvDataBaseManager;

    public ChannelDataManager(Context context) {
        mContext = context;
        mTvDataBaseManager = new TvDataBaseManager(mContext);
    }

    public long getCurrentChannelId() {
        return DataProviderManager.getLongValue(mContext, DroidLogicTvUtils.TV_DTV_CHANNEL_INDEX, -1);
    }

    public long getChannelId(Uri channelUri) {
        return DroidLogicTvUtils.getChannelId(channelUri);
    }

    public ChannelInfo getChannelInfo(Uri channelUri) {
        return mTvDataBaseManager.getChannelInfo(channelUri);
    }

    /*
     *
     * */
    public long getLastPlayChannel(String inputId) {
        long channelId = getCurrentChannelId();

        Uri channelUri = TvContract.buildChannelUri(channelId);

        ChannelInfo info = getChannelInfo(channelUri);
        if (info == null) {
            info = getFirstChannel(inputId);
        }

        if (info != null) {
            return info.getId();
        }

        return -1;
    }

    public boolean isDataChannel(Uri channelUri) {
        ChannelInfo info = getChannelInfo(channelUri);
        if (info != null) {
            return info.isData();
        }

        return false;
    }

    public boolean isRadioChannel(Uri channelUri) {
        ChannelInfo info = getChannelInfo(channelUri);
        if (info != null) {
            return ChannelInfo.isRadioChannel(info);
        }

        return false;
    }

    public boolean isChennelLocked(Uri channelUri) {
        ChannelInfo channel = getChannelInfo(channelUri);
        if (channel != null) {
            return channel.isLocked();
        }

        return false;
    }

    public boolean isCurrentChannelBlocked() {
        return DataProviderManager.getBooleanValue(mContext, DroidLogicTvUtils.TV_CURRENT_BLOCK_STATUS, false);
    }

    public boolean isCurrentChannelBlockBlocked() {
        return DataProviderManager.getBooleanValue(mContext, DroidLogicTvUtils.TV_CURRENT_CHANNELBLOCK_STATUS, false);
    }

    public void setCurrentChannelBlocked(boolean blocked) {
        DataProviderManager.putBooleanValue(mContext, DroidLogicTvUtils.TV_CURRENT_BLOCK_STATUS, blocked);
    }

    private boolean isDroidLogicInput(String inputId) {
        boolean result = false;
        if (inputId != null && inputId.startsWith(TV_DROIDLOGIC_PACKAGE)) {
            result = true;
        }

        return result;
    }

    private boolean isDtvKitInput(String inputId) {
        boolean result = false;
        if (inputId != null && inputId.startsWith(DTVKIT_PACKAGE)) {
            result = true;
        }

        return result;
    }

    private  long getLong(Context context, String key, long def) {
        long result = def;
        if (context != null) {
            result = DataProviderManager.getLongValue(context, key, def);
        }
        return result;
    }

    private boolean isAtscCountry(Context context) {
        boolean result = false;
        if (context != null) {
            String country = DroidLogicTvUtils.getCountry(mContext);
            if (TextUtils.equals(country, "US") || TextUtils.equals(country, "MX")) {
                result = true;
            }
        }

        return result;
    }

    public long getLastPlayChannelForInput(String inputId) {
        long channelId = -1;

        if (isDroidLogicInput(inputId)) {
            String signalType = DroidLogicTvUtils.getCurrentSignalType(mContext);
            if (signalType.equals(DroidLogicTvUtils.SIGNAL_TYPE_ERROR)) {
                signalType = TvContract.Channels.TYPE_ATSC_T;
            }

            if (isAtscCountry(mContext)) {
                channelId = getLong(mContext, signalType, -1);
            }
            else{
                if (!TextUtils.equals(DroidLogicTvUtils.getSearchType(mContext), "ATV")) {
                    channelId = getLong(mContext, DroidLogicTvUtils.DTV_CHANNEL_INDEX, -1);
                } else {
                    channelId = getLong(mContext, DroidLogicTvUtils.ATV_CHANNEL_INDEX, -1);
                }
            }
        } else {
            channelId = getLong(mContext, inputId, -1);
        }

        ChannelInfo info = getFirstChannel(inputId);
        if (channelId == -1){
            if (info != null) {
                channelId = info.getId();
            }
        }
        else{
            if (info == null){
                channelId = -1;  //if current source no channel
            }
        }

        if (channelId != -1){
            DataProviderManager.putLongValue(mContext, DroidLogicTvUtils.TV_DTV_CHANNEL_INDEX, channelId);
        }
        return channelId;
    }

    public boolean dtvkitChannelsMatchSystem(String channelType, String dvbSystem) {
        boolean ret = false;
        if (TextUtils.isEmpty(dvbSystem)) {
            ret = true;
        } else {
            switch (dvbSystem) {
                case "DVB-T": {
                    if (TvContract.Channels.TYPE_DVB_T.equals(channelType)
                            || TvContract.Channels.TYPE_DVB_T2.equals(channelType)) {
                        ret = true;
                    }
                    break;
                }
                case "DVB-C": {
                    if (TvContract.Channels.TYPE_DVB_C.equals(channelType)
                            || TvContract.Channels.TYPE_DVB_C2.equals(channelType)) {
                        ret = true;
                    }
                    break;
                }
                case "DVB-S": {
                    if (TvContract.Channels.TYPE_DVB_S.equals(channelType)
                            || TvContract.Channels.TYPE_DVB_S2.equals(channelType)
                            || TvContract.Channels.TYPE_DVB_SH.equals(channelType)) {
                        ret = true;
                    }
                    break;
                }
                case "ISDB-T": {
                    if (TvContract.Channels.TYPE_ISDB_T.equals(channelType)
                            || TvContract.Channels.TYPE_ISDB_TB.equals(channelType)) {
                        ret = true;
                    }
                    break;
                }
                case "ANALOG": {
                    if (TvContract.Channels.TYPE_PAL.equals(channelType)
                            || TvContract.Channels.TYPE_SECAM.equals(channelType)
                            || TvContract.Channels.TYPE_NTSC.equals(channelType)) {
                        ret = true;
                    }
                    break;
                }
            }
        }
        return ret;
    }

    public String getDtvkitSystem(Context context) {
        String result = null;
        if (context != null) {
            result = DataProviderManager.getStringValue(context, TV_DTVKIT_SYSTEM, "");
        }

        return result;
    }

    private ChannelInfo getFirstChannel(String inputId) {
        ChannelInfo channel = null;

        ArrayList<ChannelInfo> channelList = mTvDataBaseManager.getChannelList(inputId, ChannelInfo.COMMON_PROJECTION, null, null);
        if (channelList != null && channelList.size() > 0) {
            int i;
            for (i = 0; i < channelList.size(); i++) {
                channel = channelList.get(i);

                if (!TextUtils.equals(inputId, channel.getInputId())) {
                    continue;
                }

                if (isDroidLogicInput(inputId)) {
                    if (isAtscCountry(mContext)) {
                        if (!TvContract.Channels.TYPE_OTHER.equals(channel.getType())) {
                            break;
                        }
                    } else {
                        if (DroidLogicTvUtils.isATV(mContext) && channel.isAnalogChannel()) {
                            break;
                        } else if (DroidLogicTvUtils.isDTV(mContext) && channel.isDigitalChannel()) {
                            break;
                        }
                    }
                } else if (isDtvKitInput(inputId)) {
                    String dtvkitSystem = getDtvkitSystem(mContext);
                    if (!dtvkitChannelsMatchSystem(channel.getType(), dtvkitSystem)) {
                        continue;
                    }
                    if (!TvContract.Channels.TYPE_OTHER.equals(channel.getType())) {
                        break;
                    }
                }
                else {
                    if (!TvContract.Channels.TYPE_OTHER.equals(channel.getType())) {
                        break;
                    }
                }
            }

            if (i == channelList.size()) {
                channel = null;
            }
        }

        return channel;
    }
}
