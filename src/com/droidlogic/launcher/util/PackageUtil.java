package com.droidlogic.launcher.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageUtil {

    public static ApplicationInfo getApplicationInfoByPkgName(Context context, String pkgName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(pkgName, 0);
    }

}
