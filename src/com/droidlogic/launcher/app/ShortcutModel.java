package com.droidlogic.launcher.app;

public class ShortcutModel implements IAppInfo {

    public enum INTENT {
        NETWORK, LANGUAGE, TIME
    }

    private final String name;
    private final int iconRes;
    private final INTENT intent;

    public ShortcutModel(String name, int iconRes, INTENT intent) {
        this.name = name;
        this.iconRes = iconRes;
        this.intent = intent;
    }

    public String getName() {
        return name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public INTENT getIntent() {
        return intent;
    }
}
