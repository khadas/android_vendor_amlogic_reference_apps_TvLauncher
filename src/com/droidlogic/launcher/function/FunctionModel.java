
package com.droidlogic.launcher.function;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.view.View;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppUninstall;

import java.util.ArrayList;
import java.util.List;

public class FunctionModel {

    public static final String PKG_NAME_FILE_BROWSER = "com.droidlogic.FileBrower";
    public static final String PKG_NAME_TV = "com.droidlogic.android.tv";
    public static final String PKG_NAME_MIRACAST = "com.droidlogic.miracast";
    public static final String PKG_NAME_TVCAST = "com.droidlogic.tvcast";
    public static final String PKG_NAME_MEDIA_CENTER = "com.droidlogic.mediacenter";
    public static final String PKG_NAME_WEB_BROWSER = "org.chromium.webview_shell";
    public static final String PKG_NAME_ZEASN_MARKET = "zeasn.open.technical.tv.store";

    private int icon;
    private int bgColor;
    private String id;
    private int name;
    private String packageName;
    private Intent mIntent;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public static List<FunctionModel> getFunctionList(Context context) {
        List<FunctionModel> functionModels = new ArrayList<>();
        FunctionModel func;

        func = new FunctionModel();
        func.setName(R.string.function_app_filebrowser);
        func.setIcon(R.drawable.icon_file_browser);
        func.setBgColor(Color.parseColor("#1E3F76"));
        func.setPackageName(PKG_NAME_FILE_BROWSER);
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(PKG_NAME_FILE_BROWSER);
        func.setIntent(intent);
        functionModels.add(func);

        func = new FunctionModel();
        func.setName(R.string.function_app_tvcast);
        func.setIcon(R.drawable.icon_tv_cast);
        func.setBgColor(Color.parseColor("#325568"));
        func.setPackageName(PKG_NAME_TVCAST);
        intent = context.getPackageManager().getLaunchIntentForPackage(PKG_NAME_TVCAST);
        func.setIntent(intent);
        functionModels.add(func);

        func = new FunctionModel();
        func.setName(R.string.function_system_setting);
        func.setIcon(R.drawable.settings);
        func.setBgColor(Color.parseColor("#304F7D"));
        intent = new Intent(Settings.ACTION_SETTINGS);
        func.setIntent(intent);
        functionModels.add(func);

        func = new FunctionModel();
        func.setName(R.string.function_app_uninstall);
        func.setIcon(R.drawable.app_uninstall);
        func.setBgColor(Color.parseColor("#3A4070"));
        func.setIntent(new Intent(context, AppUninstall.class));
        functionModels.add(func);

        return functionModels;
    }

    public void onClickModel(View view) {
        Context context = view.getContext();
        if (mIntent != null) {
            context.startActivity(mIntent);
        }
    }

}
