package com.droidlogic.launcher.function;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;


import com.droidlogic.launcher.leanback.presenter.content.SystemFunctionPresenter;

import java.util.List;

public class FunctionRow {

    private final Context mContext;
    private final ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new SystemFunctionPresenter());

    public FunctionRow(Context context){
        mContext = context;
        load();
    }

    public ArrayObjectAdapter getListRowAdapter() {
        return mListRowAdapter;
    }

    public void load(){
        List<FunctionModel> functionModels = FunctionModel.getFunctionList(mContext);
        int cardCount = functionModels.size();
        for (int i = 0; i < cardCount; i++) {
            mListRowAdapter.add(functionModels.get(i));
        }
    }

}
