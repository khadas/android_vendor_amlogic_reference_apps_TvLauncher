package com.droidlogic.launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import com.droidlogic.launcher.R;

import java.util.List;

public class PackageUtil {

    public static ApplicationInfo getApplicationInfoByPkgName(Context context, String pkgName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(pkgName, 0);
    }


    /**
     * 点击推荐应用的打开方法,先判断是不是安装了该应用,安装了直接打开应用,没有安装跳转到商店的详情页去下载
     *
     * @param context      上下文
     * @param pkgName      要打开的应用的包名
     * @param storePkgName 商店的包名
     */
    public static void clickRecommendApp(Context context, String pkgName, String storePkgName) {
        if (isPkgInstalled(context, pkgName)) {
            startNonPartyApplication(context, pkgName);
        } else {
            startOpenStoreDetail(context, pkgName, storePkgName);
        }
    }

    /**
     * Open other apps by package name
     * 启动第三方应用
     *
     * @param context
     * @param pkgName
     */
    public static void startNonPartyApplication(Context context, String pkgName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent it = packageManager.getLaunchIntentForPackage(pkgName);
            if (null != it) {
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(it);
            } else {
                String activity_path = getClassName(context, pkgName);
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName(pkgName, activity_path);
                intent.setComponent(cn);
                if (intent.resolveActivityInfo(context.getPackageManager(), PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    context.startActivity(intent);
                }
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 打开商店的详情页
     *
     * @param context
     * @param pkgName
     */
    public static void startOpenStoreDetail(Context context, String pkgName, String storePkgName) {
        try {
            Uri uri = Uri.parse("market://details?id=" + pkgName);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(storePkgName);
            context.startActivity(intent);
        } catch (Exception e) {
            // 要是没有安装商店的提示,正常是商店是预制的,这个提示自己写一下
            Toast.makeText(context, context.getString(R.string.market_not_install), Toast.LENGTH_SHORT).show();
        }
    }

    /***
     * 本地是否安装该应用
     * @param pkgName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String pkgName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String getClassName(Context context, String pkgName) {
        String className = "";
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.setPackage(pkgName);
        List<ResolveInfo> resolveinfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        if (null != resolveinfoList && resolveinfoList.size() > 0) {
            ResolveInfo resolveinfo = resolveinfoList.iterator().next();
            if (resolveinfo != null)
                className = resolveinfo.activityInfo.name;
        }
        return className;
    }

}
