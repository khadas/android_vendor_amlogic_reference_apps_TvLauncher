package com.droidlogic.launcher.livetv;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;

import java.util.List;

public class TvRow {
    private String  mTitle;
    private Context mContext;
    private ArrayObjectAdapter mGridAdapter;
    private ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new TvCardPresenter());

    public TvRow(Context context, String title, ArrayObjectAdapter gridAdapter){
        mContext = context;
        mTitle   = title;
        mGridAdapter = gridAdapter;

        load();

        HeaderItem header = new HeaderItem(0, mTitle);
        mGridAdapter.add(new ListRow(header, mListRowAdapter));
    }

    public void load(){
        for (MediaModel mediaModel : MediaModel.getDTVModels(mContext)) {
            mListRowAdapter.add(mediaModel);
        }
    }

    public void update(){
        int i;

        List<MediaModel> list = MediaModel.getDTVModels(mContext);

        int curSize = mListRowAdapter.size();
        int newSize = list.size();
        if (curSize != newSize) {
            mListRowAdapter.clear();
            for (i = 0; i < newSize; i++) {
                MediaModel model2 = (MediaModel) list.get(i);
                mListRowAdapter.add(model2);
            }
        } else {
            for (i = 0; i < newSize; i++) {
                MediaModel model1 = (MediaModel) mListRowAdapter.get(i);
                MediaModel model2 = (MediaModel) list.get(i);
                if (model1.getId() != model2.getId()) {
                    mListRowAdapter.replace(i, model2);
                }
            }
        }
    }
}
