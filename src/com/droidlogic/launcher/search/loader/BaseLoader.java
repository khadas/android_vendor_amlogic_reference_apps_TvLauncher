package com.droidlogic.launcher.search.loader;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.util.List;

public abstract class BaseLoader<T> {

    protected Context context;

    public BaseLoader(Context context) {
        this.context = context;
    }

    protected ContentResolver getContentResolver(){
        return context.getContentResolver();
    }

    protected abstract Cursor createCursor();

    public abstract List<T> getDataList();

}
