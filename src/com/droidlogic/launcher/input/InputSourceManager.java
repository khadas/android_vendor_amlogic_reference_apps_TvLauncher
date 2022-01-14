package com.droidlogic.launcher.input;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiTvClient;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.app.tv.TvControlManager;
import com.droidlogic.app.tv.TvScanConfig;
import com.droidlogic.launcher.R;
import com.droidlogic.launcher.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InputSourceManager {
    private static String TAG = "InputSourceManager";

    private static final String PACKAGE_DROIDLOGIC_TVINPUT = "com.droidlogic.tvinput";
    private static final String PACKAGE_DROIDLOGIC_DTVKIT = "com.droidlogic.dtvkit.inputsource";
    private static final String PACKAGE_GOOGLE_VIDEOS = "com.google.android.videos";
    private static final String COMMANDACTION = "action.startlivetv.settingui";
    private static final String DTVKITSOURCE = "com.droidlogic.dtvkit.inputsource/.DtvkitTvInput/HW19";
    private static final String TVSOURCE = "com.droidlogic.tvinput/.services.ADTVInputService/HW16";

    private static final int LOGICAL_ADDRESS_AUDIO_SYSTEM = 5;
    private final boolean DEBUG = false;


    private Context mContext;
    private static HdmiTvClient mTvClient;
    private TvInputManager mTvInputManager;
    private TvControlManager mTvControlManager;
    private HdmiControlManager mHdmiControlManager;

    private static final int[] inputId = {
            TvInputInfo.TYPE_TUNER,
            TvInputInfo.TYPE_OTHER,
            TvInputInfo.TYPE_COMPOSITE,
            TvInputInfo.TYPE_SVIDEO,
            TvInputInfo.TYPE_SCART,
            TvInputInfo.TYPE_COMPONENT,
            TvInputInfo.TYPE_VGA,
            TvInputInfo.TYPE_DVI,
            TvInputInfo.TYPE_HDMI,
            TvInputInfo.TYPE_DISPLAY_PORT,
    };

//    private static int inputStringId[] = {
//            R.string.input_tuner,
//            R.string.input_other,
//            R.string.input_composite,
//            R.string.input_svideo,
//            R.string.input_scart,
//            R.string.input_component,
//            R.string.input_vga,
//            R.string.input_dvi,
//            R.string.input_hdmi,
//            R.string.input_display_port,
//    };

    private static final int[] inputIconId = {
            R.drawable.input_tuner,
            R.drawable.input_other,
            R.drawable.input_composite,
            R.drawable.input_svideo,
            R.drawable.input_scart,
            R.drawable.input_component,
            R.drawable.input_vga,
            R.drawable.input_dvi,
            R.drawable.input_hdmi,
            R.drawable.input_display_port,
    };

    private static final int[] inputConnectIconId = {
            R.drawable.input_tuner_in,
            R.drawable.input_other,
            R.drawable.input_composite_in,
            R.drawable.input_svideo,
            R.drawable.input_scart,
            R.drawable.input_component,
            R.drawable.input_vga,
            R.drawable.input_dvi,
            R.drawable.input_hdmi_in,
            R.drawable.input_display_port,
    };

    public InputSourceManager(Context context, TvInputManager.TvInputCallback callback, Handler handle) {
        mContext = context;

        mTvInputManager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
        mTvInputManager.registerCallback(callback, handle);
        mHdmiControlManager = (HdmiControlManager) context.getSystemService(Context.HDMI_CONTROL_SERVICE);
        if (mHdmiControlManager != null) {
            mTvClient = mHdmiControlManager.getTvClient();
        }
    }

    public void setSearchType(String name){
        if (DroidLogicTvUtils.isChina(mContext)) {
            if (TextUtils.equals(name, mContext.getResources().getString(R.string.input_atv))) {
                DroidLogicTvUtils.setSearchInputId(mContext, name, false); //just for force update channel
                DroidLogicTvUtils.setSearchType(mContext, TvScanConfig.TV_SEARCH_TYPE.get(TvScanConfig.TV_SEARCH_TYPE_ATV_INDEX));
            } else if (TextUtils.equals(name, mContext.getResources().getString(R.string.input_dtv))) {
                DroidLogicTvUtils.setSearchInputId(mContext, name, false); //just for force update channel
                String country = DroidLogicTvUtils.getCountry(mContext);
                ArrayList<String> dtvList = TvScanConfig.GetTvDtvSystemList(country);
                DroidLogicTvUtils.setSearchType(mContext, dtvList.get(0));
            }
        }
    }

    public boolean isAtvSearch(){
        String atv  = TvScanConfig.TV_SEARCH_TYPE.get(TvScanConfig.TV_SEARCH_TYPE_ATV_INDEX);
        String type = DroidLogicTvUtils.getSearchType(mContext);
        return TextUtils.equals(atv, type);
    }


    public String getInputName(String id){
        List<TvInputInfo> inputList = mTvInputManager.getTvInputList();
        if (inputList == null) {
            return null;
        }

        List<HdmiDeviceInfo> hdmiList = getHdmiList();
        HdmiDeviceInfo audioSystem = getOrigHdmiDevice(LOGICAL_ADDRESS_AUDIO_SYSTEM, hdmiList);

        for (TvInputInfo input : inputList) {
            if (input.getId().equals(id)) {
                CharSequence name = getTitle(mContext, input, audioSystem, hdmiList);
                return name.toString();
            }
        }

        return null;
    }

    public void switchInput(String id, String name) {
        List<TvInputInfo> inputList = mTvInputManager.getTvInputList();
        if (inputList == null) {
            return;
        }

        if (id == null) {
            id = TVSOURCE;
        }

        if (name == null) {
            name = "DTV";
        }

        for (TvInputInfo input : inputList) {
            if (input.getId().equals(id)) {
                DroidLogicTvUtils.setCurrentInputId(mContext, id);
                DroidLogicTvUtils.setSearchInputId(mContext, input.getId(), false);
                if (!input.isPassthroughInput()) {
                    if (TextUtils.equals(name, mContext.getResources().getString(R.string.input_atv))) {
                        DroidLogicTvUtils.setSearchType(mContext, TvScanConfig.TV_SEARCH_TYPE.get(TvScanConfig.TV_SEARCH_TYPE_ATV_INDEX));
                    } else if (TextUtils.equals(name, mContext.getResources().getString(R.string.input_dtv))) {
                        String country = DroidLogicTvUtils.getCountry(mContext);
                        ArrayList<String> dtvList = TvScanConfig.GetTvDtvSystemList(country);
                        DroidLogicTvUtils.setSearchType(mContext, dtvList.get(0));
                    }
                }

                Settings.System.putInt(mContext.getContentResolver(), DroidLogicTvUtils.TV_CURRENT_DEVICE_ID,
                        DroidLogicTvUtils.getHardwareDeviceId(input));

                SystemControlManager mSystemControlManager = SystemControlManager.getInstance();
                if (DTVKITSOURCE.equals(input.getId())) {//DTVKIT SOURCE
                    if (DEBUG) Logger.d(TAG, "DtvKit source");
                    mSystemControlManager.SetDtvKitSourceEnable(1);
                } else {
                    if (DEBUG) Logger.d(TAG, "Not DtvKit source");
                    mSystemControlManager.SetDtvKitSourceEnable(0);
                }

                break;
            }
        }
    }

    public void startInputAPP(String id) {
        try {
            if (id == null) {
                id = DroidLogicTvUtils.getCurrentInputId(mContext);;
            }

            Intent intent = new Intent(TvInputManager.ACTION_SETUP_INPUTS);
            intent.putExtra("from_tv_source", true);
            intent.putExtra(TvInputInfo.EXTRA_INPUT_ID, id);
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Logger.e(TAG, " can't start LiveTv:" + e);
        }
    }


    public List<InputInfo> getInputList() {
        List<InputInfo> InputInfos = new ArrayList<>();

        List<TvInputInfo> input_list = mTvInputManager.getTvInputList();
        if (input_list == null) {
            return InputInfos;
        }
        Collections.sort(input_list, new InputsComparator());
        List<HdmiDeviceInfo> hdmiList = getHdmiList();
        HdmiDeviceInfo audioSystem = getOrigHdmiDevice(LOGICAL_ADDRESS_AUDIO_SYSTEM, hdmiList);

        TvInputInfo currentInfo = null;
        InputInfo input;
        for (TvInputInfo tvInput : input_list) {
            //Logger.d(TAG, "input:" + tvInput.getType());

            if (tvInput.isHidden(mContext)) {
                Logger.d(TAG, "this input hidden");
                continue;
            }

            if (tvInput.isPassthroughInput() && tvInput.getParentId() != null) {
                // DroidSettings always show the fixed hdmi port related sources, even though
                // there are no devices connected, so we should only care about the parent
                // sources.
                continue;
            }

            CharSequence name = getTitle(mContext, tvInput, audioSystem, hdmiList);
            boolean connect = isInputEnabled(tvInput);
            //Logger.d(TAG, "input:" + name + " connect:"+connect);
            int icon = getIcon(tvInput, connect);
            input = new InputInfo(tvInput.getId(), name.toString(), icon);
            InputInfos.add(input);

            input = getSpecificDtv(mContext, tvInput);
            if (input != null){
                InputInfos.add(input);
            }
        }
        return InputInfos;
    }

    private InputInfo getSpecificDtv(Context themedContext, TvInputInfo input) {
        if (DroidLogicTvUtils.isChina(themedContext)
                && input.getType() == TvInputInfo.TYPE_TUNER
                && PACKAGE_DROIDLOGIC_TVINPUT.equals(input.getServiceInfo().packageName)) {

            String name = themedContext.getString(R.string.input_dtv);
            boolean connect = isInputEnabled(input);
            Logger.d(TAG, "input:" + name + " connect:"+connect);
            int icon = getIcon(input, connect);
            InputInfo info = new InputInfo(input.getId(), name.toString(), icon);
            return info;
        }
        return null;
    }

//    private static int getInputString(int id){
//        int i;
//        for(i=0; i<inputId.length; i++){
//            if(id == inputId[i]){
//                return inputStringId[i];
//            }
//        }
//
//        return inputStringId[1]; //other
//    }

    private int getInputIcon(int id, boolean isConnected) {
        int i;
        for (i = 0; i < inputId.length; i++) {
            if (id == inputId[i]) {
                if (isConnected) {
                    return inputConnectIconId[i];
                } else {
                    return inputIconId[i];
                }
            }
        }

        return inputIconId[1]; //other
    }


    public int getIcon(TvInputInfo info, boolean isConnected) {
        int icon = getInputIcon(info.getType(), isConnected);
        return icon;
    }

    public CharSequence getTitle(Context themedContext, TvInputInfo input, HdmiDeviceInfo audioSystem, List<HdmiDeviceInfo> hdmiList) {
        CharSequence title = "";
        CharSequence label = input.loadLabel(themedContext);
        CharSequence customLabel = input.loadCustomLabel(themedContext);
        if (TextUtils.isEmpty(customLabel) || customLabel.equals(label)) {
            title = label;
        } else {
            title = customLabel;
        }
        //Logger.d(TAG, "getTitle default " + title + ", label = " + label + ", customLabel = " + customLabel);
        if (input.isPassthroughInput()) {
            int portId = DroidLogicTvUtils.getPortId(input);
            if (audioSystem != null && audioSystem.getPortId() == portId) {
                // there is an audiosystem connected.
                title = audioSystem.getDisplayName();
            } else {
                HdmiDeviceInfo hdmiDevice = getOrigHdmiDeviceByPort(portId, hdmiList);
                if (hdmiDevice != null) {
                    // there is a playback connected.
                    title = hdmiDevice.getDisplayName();
                }
            }
        } else if (input.getType() == TvInputInfo.TYPE_TUNER) {
            title = getTitleForTuner(themedContext, input.getServiceInfo().packageName, title, input);
        } else if (TextUtils.isEmpty(title)) {
            title = input.getServiceInfo().name;
        }
        //Logger.d(TAG, "getTitle " + title);
        return title;
    }

    private CharSequence getTitleForTuner(Context themedContext, String packageName, CharSequence label, TvInputInfo input) {
        CharSequence title = label;
        if (PACKAGE_DROIDLOGIC_TVINPUT.equals(packageName)) {
            title = themedContext.getString(DroidLogicTvUtils.isChina(themedContext) ? R.string.input_atv : R.string.input_long_label_for_tuner);
        } else if (TextUtils.isEmpty(label)) {
            if (PACKAGE_DROIDLOGIC_DTVKIT.equals(packageName)) {
                title = themedContext.getString(R.string.input_dtv_kit);
            } else if (PACKAGE_GOOGLE_VIDEOS.equals(packageName)) {
                title = themedContext.getString(R.string.input_google_channel);
            } else {
                title = input.getServiceInfo().name;
            }
        }

        //Logger.d(TAG, "getTitleForTuner title " + title + " for package " + packageName);
        return title;
    }

    private List<HdmiDeviceInfo> getHdmiList() {
        if (mTvClient == null) {
            Logger.e(TAG, "mTvClient null!");
            return null;
        }
        return mTvClient.getDeviceList();
    }

    /**
     * The update of hdmi device info will not notify TvInputManagerService now.
     */
    private HdmiDeviceInfo getOrigHdmiDeviceByPort(int portId, List<HdmiDeviceInfo> hdmiList) {
        if (hdmiList == null) {
            Logger.d(TAG, "mTvInputManager or mTvClient maybe null");
            return null;
        }
        for (HdmiDeviceInfo info : hdmiList) {
            if (info.getPortId() == portId) {
                return info;
            }
        }
        return null;
    }

    private HdmiDeviceInfo getOrigHdmiDevice(int logicalAddress, List<HdmiDeviceInfo> hdmiList) {
        if (hdmiList == null) {
            Logger.d(TAG, "mTvInputManager or mTvClient maybe null");
            return null;
        }
        for (HdmiDeviceInfo info : hdmiList) {
            if ((info.getLogicalAddress() == logicalAddress)) {
                return info;
            }
        }
        return null;
    }

    private boolean isInputEnabled(TvInputInfo input) {
        HdmiDeviceInfo hdmiInfo = input.getHdmiDeviceInfo();
        if (hdmiInfo != null) {
            //if (DEBUG) Logger.d(TAG, "isInputEnabled:  hdmiInfo="+ hdmiInfo);
            return true;
        }

        int deviceId = DroidLogicTvUtils.getHardwareDeviceId(input);
//        if (DEBUG) {
//            Loggerd(TAG, "===== getHardwareDeviceId:tvInputId = " + input.getId());
//            Loggerd(TAG, "===== deviceId : "+ deviceId);
//        }
        TvControlManager.SourceInput tvSourceInput = DroidLogicTvUtils.parseTvSourceInputFromDeviceId(deviceId);
        int connectStatus = -1;
        if (tvSourceInput != null) {
            connectStatus = mTvInputManager.getInputState(input.getId());
        } else {
//            if (DEBUG) {
//                Loggerw(TAG, "===== cannot find tvSourceInput");
//            }
        }

        return !input.isPassthroughInput() || mTvInputManager.INPUT_STATE_CONNECTED == connectStatus || deviceId == DroidLogicTvUtils.DEVICE_ID_SPDIF;
    }

    private class InputsComparator implements Comparator<TvInputInfo> {
        @Override
        public int compare(TvInputInfo lhs, TvInputInfo rhs) {
            if (lhs == null) {
                return (rhs == null) ? 0 : 1;
            }
            if (rhs == null) {
                return -1;
            }

           /* boolean enabledL = isInputEnabled(lhs);
            boolean enabledR = isInputEnabled(rhs);
            if (enabledL != enabledR) {
                return enabledL ? -1 : 1;
            }*/

            int priorityL = getPriority(lhs);
            int priorityR = getPriority(rhs);
            if (priorityL != priorityR) {
                return priorityR - priorityL;
            }

            String customLabelL = (String) lhs.loadCustomLabel(mContext);
            String customLabelR = (String) rhs.loadCustomLabel(mContext);
            if (!TextUtils.equals(customLabelL, customLabelR)) {
                customLabelL = customLabelL == null ? "" : customLabelL;
                customLabelR = customLabelR == null ? "" : customLabelR;
                return customLabelL.compareToIgnoreCase(customLabelR);
            }

            String labelL = (String) lhs.loadLabel(mContext);
            String labelR = (String) rhs.loadLabel(mContext);
            labelL = labelL == null ? "" : labelL;
            labelR = labelR == null ? "" : labelR;
            return labelL.compareToIgnoreCase(labelR);
        }

        private int getPriority(TvInputInfo info) {
            switch (info.getType()) {
                case TvInputInfo.TYPE_TUNER:
                    return 9;
                case TvInputInfo.TYPE_HDMI:
                    HdmiDeviceInfo hdmiInfo = info.getHdmiDeviceInfo();
                    if (hdmiInfo != null && hdmiInfo.isCecDevice()) {
                        return 8;
                    }
                    return 7;
                case TvInputInfo.TYPE_DVI:
                    return 6;
                case TvInputInfo.TYPE_COMPONENT:
                    return 5;
                case TvInputInfo.TYPE_SVIDEO:
                    return 4;
                case TvInputInfo.TYPE_COMPOSITE:
                    return 3;
                case TvInputInfo.TYPE_DISPLAY_PORT:
                    return 2;
                case TvInputInfo.TYPE_VGA:
                    return 1;
                case TvInputInfo.TYPE_SCART:
                default:
                    return 0;
            }
        }
    }

}
