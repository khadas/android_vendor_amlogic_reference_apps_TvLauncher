package com.droidlogic.launcher.app;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import java.util.ArrayList;


public class AppRow {
    private String  mTitle;
    private Context mContext;
    private ArrayObjectAdapter mGridAdapter;
    private ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new AppCardPresenter());

    public AppRow(Context context, String title, ArrayObjectAdapter gridAdapter){
        mContext = context;
        mTitle = title;
        mGridAdapter = gridAdapter;
        load();
        HeaderItem header = new HeaderItem(0, mTitle);
        mGridAdapter.add(new ListRow(header, mListRowAdapter));
    }

    public void load(){
        ArrayList<AppModel> appDataList = new AppDataManage(mContext).getAppsList();
        int cardCount = appDataList.size();

        for (int i = 0; i < cardCount; i++) {
            mListRowAdapter.add(appDataList.get(i));
        }
    }

    public void add(String packageName){
        AppModel model = new AppDataManage(mContext).getLaunchAppModel(packageName);
        if (model != null) {
            mListRowAdapter.add(model);
        }
    }

    public void update(String packageName){
        int i;
        AppModel newModel = new AppDataManage(mContext).getLaunchAppModel(packageName);

        for (i = 0; i < mListRowAdapter.size(); i++) {
            AppModel model = (AppModel) mListRowAdapter.get(i);
            if (model.getPackageName().equals(packageName)) {
                mListRowAdapter.replace(i, newModel);
                break;
            }
        }
    }

    public void remove(String packageName){
        int i;
        for (i = 0; i < mListRowAdapter.size(); i++) {
            AppModel model = (AppModel) mListRowAdapter.get(i);
            if (model.getPackageName().equals(packageName)) {
                mListRowAdapter.removeItems(i, 1);
                break;
            }
        }
    }

    public void update(){
        int i;

        ArrayList<AppModel> list = new AppDataManage(mContext).getAppsList();

        int curSize = mListRowAdapter.size();
        int newSize = list.size();
        if (curSize != newSize) {
            mListRowAdapter.clear();
            for (i = 0; i < newSize; i++) {
                AppModel model2 = (AppModel) list.get(i);
                mListRowAdapter.add(model2);
            }
        } else {
            for (i = 0; i < newSize; i++) {
                AppModel model1 = (AppModel) mListRowAdapter.get(i);
                AppModel model2 = (AppModel) list.get(i);
                if (!model1.getPackageName().equals(model2.getPackageName())) {
                    mListRowAdapter.replace(i, model2);
                }
            }
        }
    }
}
