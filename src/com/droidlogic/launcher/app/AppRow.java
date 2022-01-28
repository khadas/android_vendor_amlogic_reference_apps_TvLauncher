package com.droidlogic.launcher.app;

import android.content.Context;

import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;

import com.droidlogic.launcher.leanback.presenter.content.AppCardPresenter;

import java.util.ArrayList;
import java.util.List;


public class AppRow {

    private final int APP_MAX_SHOW_NUM = 5;
    private String mTitle;
    private Context mContext;
    private ArrayObjectAdapter mGridAdapter;
    private ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new AppCardPresenter());

    public AppRow(Context context, String title, ArrayObjectAdapter gridAdapter) {
        mContext = context;
        mTitle = title;
        mGridAdapter = gridAdapter;
        load();
        HeaderItem header = new HeaderItem(0, mTitle);
        mGridAdapter.add(new ListRow(header, mListRowAdapter));
    }

    private void generateAppMoreModel(List<AppModel> appModelList) {
        AppMoreModel appMoreModel = new AppMoreModel();
        if (appModelList != null) {
            for (AppModel appModel : appModelList) {
                appMoreModel.addAppModel(appModel);
            }
        }

        if (appMoreModel.getAppModelList().size() > 0) {
            mListRowAdapter.add(appMoreModel);
        }
    }

    private void generateAppRowData(ArrayList<AppModel> appDataList) {
        int cardCount = appDataList.size();
        for (int i = 0; i < cardCount; i++) {
            if (i < APP_MAX_SHOW_NUM) {
                mListRowAdapter.add(appDataList.get(i));
            } else {
                mListRowAdapter.remove(appDataList.get(APP_MAX_SHOW_NUM - 1));
                generateAppMoreModel(appDataList.subList(APP_MAX_SHOW_NUM - 1, cardCount));
                break;
            }
        }
    }

    private void load() {
        ArrayList<AppModel> appDataList = new AppDataManage(mContext).getAppsList();
        mListRowAdapter.clear();
        generateAppRowData(appDataList);
    }

    public void update() {

        ArrayList<AppModel> list = new AppDataManage(mContext).getAppsList();
        int newSize = list.size();
        int adapterSize = getAdapterSize();
        boolean adapterSizeChanged = false;

        if (newSize < adapterSize) {
            int shouldRemoveCount = adapterSize - newSize;
            mListRowAdapter.removeItems(adapterSize - shouldRemoveCount, shouldRemoveCount);
            adapterSizeChanged = true;
        }

        for (int i = 0; i < newSize; i++) {

            AppModel model2 = (AppModel) list.get(i);

            if (adapterSizeChanged) {
                adapterSizeChanged = false;
                adapterSize = getAdapterSize();
            }

            if (i < adapterSize) {
                if (i < APP_MAX_SHOW_NUM) {
                    mListRowAdapter.replace(i, model2);
                } else {
                    mListRowAdapter.remove(mListRowAdapter.get(i - 1));
                    generateAppMoreModel(list.subList(APP_MAX_SHOW_NUM - 1, list.size()));
                    break;
                }
            } else {
                adapterSizeChanged = true;
                if (i < APP_MAX_SHOW_NUM) {
                    mListRowAdapter.add(model2);
                } else {
                    mListRowAdapter.remove(mListRowAdapter.get(i - 1));
                    generateAppMoreModel(list.subList(APP_MAX_SHOW_NUM - 1, list.size()));
                    break;
                }
            }

        }
    }

    private int getAdapterSize() {
        int size = 0;
        for (int i = 0; i < mListRowAdapter.size(); i++) {
            Object item = mListRowAdapter.get(i);
            if (item instanceof AppModel) {
                size++;
            } else if (item instanceof AppMoreModel) {
                AppMoreModel appMoreModel = (AppMoreModel) item;
                size += appMoreModel.getAppModelList().size();
            }
        }
        return size;
    }
}
