package com.droidlogic.launcher.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.HashMap;
import java.util.List;

import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_TVCAST;

public class AppUtils {

    private static final HashMap<String, String> WHITE_LIST = new HashMap<String, String>() {
        {
            put(PKG_NAME_TVCAST, PKG_NAME_TVCAST);
        }
    };

    public static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    public static long getAvailMemory(Context context) {
        ActivityManager.MemoryInfo memoryInfo = getMemoryInfo(context);
        return memoryInfo.availMem;
    }

    public static void killRunningProcesses(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcess = activityManager.getRunningAppProcesses();
        if (runningAppProcess == null || runningAppProcess.size() == 0) return;
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        for (ActivityManager.RunningAppProcessInfo process : runningAppProcess) {
            for (ApplicationInfo appInfo : appList) {
                if (!appInfo.packageName.equals(context.getPackageName())
                        && !WHITE_LIST.containsKey(appInfo.packageName)
                        && (appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                        && process.processName.equals(appInfo.processName)) {
                    activityManager.forceStopPackage(appInfo.packageName);
                    break;
                }
            }
        }
    }

}
