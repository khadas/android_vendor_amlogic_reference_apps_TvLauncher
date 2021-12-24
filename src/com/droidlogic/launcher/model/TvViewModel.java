package com.droidlogic.launcher.model;

import android.content.Context;
import android.content.Intent;

import com.droidlogic.launcher.function.FunctionModel;

public class TvViewModel {

    Intent intent;

    public void lunch(Context context) {
        if (intent == null) {
            intent = context.getPackageManager().getLaunchIntentForPackage(FunctionModel.PKG_NAME_TV);
        }
        if (intent != null) {
            context.startActivity(intent);
        }
    }

}
