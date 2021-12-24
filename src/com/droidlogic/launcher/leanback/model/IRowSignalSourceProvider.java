package com.droidlogic.launcher.leanback.model;

import android.support.v17.leanback.widget.ArrayObjectAdapter;

public interface IRowSignalSourceProvider {

    void update();

    ArrayObjectAdapter getListRowAdapter();

}
