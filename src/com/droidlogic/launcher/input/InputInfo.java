package com.droidlogic.launcher.input;

public class InputInfo {
    public String name;
    public String id;
    public int    icon;

    public InputInfo(String id, String name, int icon){
        this.id   = id;
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }
}
