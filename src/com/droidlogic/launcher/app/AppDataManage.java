
package com.droidlogic.launcher.app;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_FILE_BROWSER;
import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_MEDIA_CENTER;
import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_MIRACAST;
import static com.droidlogic.launcher.function.FunctionModel.PKG_NAME_TVCAST;

public class AppDataManage {
    private final Context mContext;

    private final String[] mHideAppName = {
            "com.droidlogic.exoplayer2.demo",
            "com.droidlogic.launcher",
            "com.droidlogic.appinstall",
            "com.droidlogic.android.tv",
            "org.chromium.webview_shell",
            PKG_NAME_FILE_BROWSER,
            PKG_NAME_TVCAST,
            PKG_NAME_MIRACAST,
            PKG_NAME_MEDIA_CENTER
    };

    public AppDataManage(Context context) {
        mContext = context;
    }


    public HashMap<String, Integer> getAPPUseList() {
        final UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) {
            return null;
        }

        long end = System.currentTimeMillis();
        HashMap<String, Integer> map = new HashMap();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_COUNT, -1, end);
        if (usageStatsList != null) {
            for (UsageStats stat : usageStatsList) {
                map.put(stat.getPackageName(), stat.mLaunchCount);
            }
        }

        return map;
    }


    public ArrayList<AppModel> getAppsList() {
        LauncherApps mLauncherApps = (LauncherApps) mContext.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<AppModel> localArrayList = new ArrayList<>();
        final List<LauncherActivityInfo> apps = mLauncherApps.getActivityList(null, android.os.Process.myUserHandle());
        if (apps == null) {
            return localArrayList;
        }
        Collections.sort(apps, getAppNameComparator());

        final int iconDpi = mActivityManager.getLauncherLargeIconDensity();

        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                LauncherActivityInfo info = apps.get(i);
                AppModel localAppBean = new AppModel();
                Class<LauncherActivityInfo> cls = LauncherActivityInfo.class;
                try {
                    Field fileInfo = cls.getDeclaredField("mActivityInfo");
                    fileInfo.setAccessible(true);
                    ActivityInfo activityInfo = (ActivityInfo) fileInfo.get(info);
                    Drawable banner = activityInfo.loadBanner(mContext.getPackageManager());
                    localAppBean.setBanner(banner);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                localAppBean.setIcon(info.getBadgedIcon(iconDpi));
                localAppBean.setName(info.getLabel().toString());
                localAppBean.setPackageName(info.getComponentName().getPackageName());
                localAppBean.setLauncherName(info.getComponentName().getPackageName());
                //Logger.d("APP", "---GET APP:" + localAppBean.getPackageName());
                if (!isHideApp(localAppBean.getPackageName())) {
                    localArrayList.add(localAppBean);
                }
            }
        }

        return localArrayList;
    }


    public ArrayList<AppModel> getLaunchAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppModel> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        localArrayList = new ArrayList<>();
        if (localList.size() != 0) {
            localIterator = localList.iterator();
        }
        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            AppModel localAppBean = new AppModel();
            localAppBean.setBanner(localResolveInfo.activityInfo.loadBanner(localPackageManager));
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            localAppBean.setLauncherName(localResolveInfo.activityInfo.name);
            String pkgName = localResolveInfo.activityInfo.packageName;
            PackageInfo mPackageInfo;
            try {
                mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                    localAppBean.setSysApp(true);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            //Logger.d("APP", "GET APP:" + localAppBean.getPackageName());
            if (!isHideApp(localAppBean.getPackageName())) {
                localArrayList.add(localAppBean);
            }
        }
        return localArrayList;
    }

    public AppModel getLaunchAppModel(String packName) {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppModel> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        localArrayList = new ArrayList<>();
        if (localList.size() != 0) {
            localIterator = localList.iterator();
        }
        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            String pkgName = localResolveInfo.activityInfo.packageName;
            if (!packName.equals(pkgName)) {
                continue;
            }

            AppModel localAppBean = new AppModel();
            localAppBean.setBanner(localResolveInfo.activityInfo.loadBanner(localPackageManager));
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            localAppBean.setLauncherName(localResolveInfo.activityInfo.name);

            PackageInfo mPackageInfo;
            try {
                mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                    localAppBean.setSysApp(true);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            //Logger.d("APP", "GET APP:" + localAppBean.getPackageName());
            return localAppBean;
        }
        return null;
    }


    public ArrayList<AppModel> getUninstallAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppModel> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        if (localList != null) {
            localArrayList = new ArrayList<>();
            localIterator = localList.iterator();
        }
        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            AppModel localAppBean = new AppModel();
            localAppBean.setBanner(localResolveInfo.activityInfo.loadBanner(localPackageManager));
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            String pkgName = localResolveInfo.activityInfo.packageName;
            PackageInfo mPackageInfo;
            try {
                mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0) {// 系统预装
                    localAppBean.setSysApp(true);
                } else {
                    localArrayList.add(localAppBean);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return localArrayList;
    }


    private boolean isHideApp(String packName) {
        for (String name : mHideAppName) {
            if (name.equals(packName))
                return true;
        }
        return false;
    }

    public ArrayList<AppModel> getAutoRunAppList() {
        PackageManager localPackageManager = mContext.getPackageManager();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> localList = localPackageManager.queryIntentActivities(localIntent, 0);
        ArrayList<AppModel> localArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        if (localList != null) {
            localArrayList = new ArrayList<>();
            localIterator = localList.iterator();
        }

        while (true) {
            if (!localIterator.hasNext())
                break;
            ResolveInfo localResolveInfo = localIterator.next();
            AppModel localAppBean = new AppModel();
            localAppBean.setBanner(localResolveInfo.activityInfo.loadBanner(localPackageManager));
            localAppBean.setIcon(localResolveInfo.activityInfo.loadIcon(localPackageManager));
            localAppBean.setName(localResolveInfo.activityInfo.loadLabel(localPackageManager).toString());
            localAppBean.setPackageName(localResolveInfo.activityInfo.packageName);
            localAppBean.setDataDir(localResolveInfo.activityInfo.applicationInfo.publicSourceDir);
            String pkgName = localResolveInfo.activityInfo.packageName;
            String permission = "android.permission.RECEIVE_BOOT_COMPLETED";
            try {
                PackageInfo mPackageInfo = mContext.getPackageManager().getPackageInfo(pkgName, 0);
                if ((PackageManager.PERMISSION_GRANTED == localPackageManager.checkPermission(permission, pkgName))
                        && !((mPackageInfo.applicationInfo.flags & mPackageInfo.applicationInfo.FLAG_SYSTEM) > 0)) {
                    localArrayList.add(localAppBean);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return localArrayList;
    }

    private final Comparator<LauncherActivityInfo> getAppNameComparator() {
        final Collator collator = Collator.getInstance();
        final HashMap<String, Integer> map = getAPPUseList();

        return new Comparator<LauncherActivityInfo>() {

            public final int compare(LauncherActivityInfo a, LauncherActivityInfo b) {
                if (a.getUser().equals(b.getUser())) {
                    int num1 = 0;
                    int num2 = 0;
                    Integer n1 = map.get(a.getComponentName().getPackageName());
                    Integer n2 = map.get(b.getComponentName().getPackageName());

                    if (n1 != null) {
                        num1 = n1;
                    }
                    if (n2 != null) {
                        num2 = n2;
                    }

                    if (num1 > num2)
                        return -1;
                    else if (num1 < num2)
                        return 1;

                    int result = collator.compare(a.getLabel().toString(), b.getLabel().toString());
                    if (result == 0) {
                        result = a.getName().compareTo(b.getName());
                    }
                    return result;
                } else {
                    // TODO: Order this based on profile type rather than string compares.
                    return a.getUser().toString().compareTo(b.getUser().toString());
                }
            }
        };
    }
}
