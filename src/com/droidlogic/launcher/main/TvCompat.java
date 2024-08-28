package com.droidlogic.launcher.main;

import android.content.Context;
import android.media.tv.TvView;
import android.text.TextUtils;
import android.widget.TextView;

import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.app.tv.TvDataBaseManager;
import com.droidlogic.launcher.input.InputSourceManager;
import com.droidlogic.launcher.livetv.TvConfig;
import com.droidlogic.launcher.livetv.TvControl;
import com.droidlogic.launcher.util.Logger;

public class TvCompat {

    public static void setCurrentInputId(Context context, InputSourceManager inputSourceManager) {
        try {
            Class.forName("com.droidlogic.app.tv.DroidLogicTvUtils");
            if (TextUtils.isEmpty(DroidLogicTvUtils.getCurrentInputId(context))) {
                DroidLogicTvUtils.setCurrentInputId(context, inputSourceManager.getInputList().get(0).getId());
            }
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
    }

    public static TvControl buildTvControl(Context context, TvView mTvView, TextView prompt, InputSourceManager inputSourceManager) {
        TvControl tvControl = null;
        try {
            Class.forName("com.droidlogic.app.tv.TvDataBaseManager");
            tvControl = new TvControl(context, mTvView, prompt, inputSourceManager);
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
        return tvControl;
    }

    public static TvConfig buildTvConfig(Context context) {
        TvConfig tvConfig = null;
        try {
            Class.forName("com.droidlogic.app.tv.DroidLogicTvUtils");
            tvConfig = new TvConfig(context);
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
        return tvConfig;
    }

    public static String getCurrentInputId(Context context) {
        String currentInputId = null;
        try {
            Class.forName("com.droidlogic.app.tv.DroidLogicTvUtils");
            currentInputId = DroidLogicTvUtils.getCurrentInputId(context);
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
        return currentInputId;
    }

    public static boolean getPropertyBoolean(String property, boolean defValue) {
        boolean result = false;
        try {
            Class.forName("com.droidlogic.app.SystemControlManager");
            result = SystemControlManager.getInstance().getPropertyBoolean(property, defValue);
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
        return result;
    }

    public static SystemControlManager getSystemControlManager() {
        SystemControlManager systemControlManager = null;
        try {
            Class.forName("com.droidlogic.app.SystemControlManager");
            systemControlManager = SystemControlManager.getInstance();
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
        return systemControlManager;
    }

    public static TvDataBaseManager buildTvDataBaseManager(Context context) {
        TvDataBaseManager tvDataBaseManager = null;
        try {
            Class.forName("com.droidlogic.app.tv.TvDataBaseManager");
            tvDataBaseManager = new TvDataBaseManager(context);
        } catch (ClassNotFoundException e) {
            Logger.w(String.valueOf(e));
        }
        return tvDataBaseManager;
    }
}
