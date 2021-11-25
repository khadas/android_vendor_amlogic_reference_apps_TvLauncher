package com.droidlogic.launcher.input;

import java.util.ArrayList;
import java.util.List;

public class InputModel {
    public static  String TAG = "InputModel";

    private int icon;
    private String id;
    private String name;


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

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }


    public static List<InputModel> getInputList(InputSourceManager manager) {
        List<InputModel> InputModels = new ArrayList<>();

        List<InputInfo> input_list = manager.getInputList();
        InputModel func;

        for (InputInfo info : input_list) {
            int icon    = info.getIcon();

            func = new InputModel();
            func.setName(info.getName());
            func.setIcon(icon);
            func.setId(info.getId());
            InputModels.add(func);
        }

        return InputModels;
    }

}
