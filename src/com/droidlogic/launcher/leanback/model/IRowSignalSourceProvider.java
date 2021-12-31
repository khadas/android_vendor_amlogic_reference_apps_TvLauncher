package com.droidlogic.launcher.leanback.model;

import androidx.leanback.widget.ArrayObjectAdapter;

public interface IRowSignalSourceProvider {

    void update();

    ArrayObjectAdapter getListRowAdapter();

}
