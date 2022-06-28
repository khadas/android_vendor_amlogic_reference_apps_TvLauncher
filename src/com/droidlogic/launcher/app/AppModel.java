
package com.droidlogic.launcher.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.view.View;

import java.util.Objects;

public class AppModel implements IAppInfo {

    private String dataDir;
    private Drawable icon;
    private Drawable banner;
    private String id;
    private String name;
    private String launcherName;
    private String packageName;
    private int pageIndex;
    private int position;
    private boolean sysApp;
    private boolean leanbackOnly;
    private UserHandle user;

    public String getDataDir() {
        return this.dataDir;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public Drawable getBanner() {
        return banner;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public int getPosition() {
        return this.position;
    }

    public void setDataDir(String paramString) {
        this.dataDir = paramString;
    }

    public void setIcon(Drawable paramDrawable) {
        this.icon = paramDrawable;
    }

    public void setBanner(Drawable banner) {
        this.banner = banner;
    }

    public void setId(String paramString) {
        this.id = paramString;
    }

    public void setName(String paramString) {
        this.name = paramString;
    }

    public void setPackageName(String paramString) {
        this.packageName = paramString;
    }

    public void setPageIndex(int paramInt) {
        this.pageIndex = paramInt;
    }

    public void setPosition(int paramInt) {
        this.position = paramInt;
    }

    public String toString() {
        return "AppBean [packageName=" + this.packageName + ", name=" + this.name + ", dataDir=" + this.dataDir + "]";
    }

    public boolean isSysApp() {
        return sysApp;
    }

    public void setSysApp(boolean sysApp) {
        this.sysApp = sysApp;
    }

    public String getLauncherName() {
        return launcherName;
    }

    public void setLauncherName(String launcherName) {
        this.launcherName = launcherName;
    }

    public boolean isLeanbackOnly() {
        return leanbackOnly;
    }

    public void setLeanbackOnly(boolean leanbackOnly) {
        this.leanbackOnly = leanbackOnly;
    }

    public UserHandle getUser() {
        return user;
    }

    public void setUser(UserHandle user) {
        this.user = user;
    }

    public void onClickModel(View view) {
        Context context = view.getContext();
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            ComponentName componentName = new ComponentName(packageName, launcherName);
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppModel appModel = (AppModel) o;
        return packageName.equals(appModel.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName);
    }
}
