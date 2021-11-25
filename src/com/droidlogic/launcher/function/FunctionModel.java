
package com.droidlogic.launcher.function;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.droidlogic.launcher.R;
import com.droidlogic.launcher.app.AppUninstall;

import java.util.ArrayList;
import java.util.List;

public class FunctionModel {

    private int icon;
    private String id;
    private int name;
    private Intent mIntent;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getName() { return name; }

    public void setName(int name) {
        this.name = name;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    public static List<FunctionModel> getFunctionList(Context context) {
        List<FunctionModel> functionModels = new ArrayList<>();
        FunctionModel func;

        func = new FunctionModel();
        func.setName(R.string.function_system_setting);
        func.setIcon(R.drawable.settings);
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        func.setIntent(intent);
        functionModels.add(func);

        func = new FunctionModel();
        func.setName(R.string.function_app_uninstall);
        func.setIcon(R.drawable.app_uninstall);
        func.setIntent(new Intent(context, AppUninstall.class));
        functionModels.add(func);

        return functionModels;
    }
}
