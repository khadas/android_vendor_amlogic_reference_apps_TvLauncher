package com.droidlogic.launcher.function;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;


import java.util.List;

public class FunctionRow {
    private String  mTitle;
    private Context mContext;
    private ArrayObjectAdapter mGridAdapter;
    private ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new FunctionCardPresenter());

    public FunctionRow(Context context, String title, ArrayObjectAdapter gridAdapter){
        mContext = context;
        mTitle   = title;
        mGridAdapter = gridAdapter;

        load();

        HeaderItem header = new HeaderItem(0, mTitle);
        mGridAdapter.add(new ListRow(header, mListRowAdapter));
    }

    public void load(){
        List<FunctionModel> functionModels = FunctionModel.getFunctionList(mContext);
        int cardCount = functionModels.size();
        for (int i = 0; i < cardCount; i++) {
            mListRowAdapter.add(functionModels.get(i));
        }
    }

}
