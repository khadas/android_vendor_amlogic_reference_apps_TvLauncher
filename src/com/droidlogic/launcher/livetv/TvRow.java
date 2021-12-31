package com.droidlogic.launcher.livetv;

import android.content.Context;
import androidx.leanback.widget.ArrayObjectAdapter;

import com.droidlogic.launcher.leanback.model.IRowSignalSourceProvider;
import com.droidlogic.launcher.leanback.presenter.content.TvCardPresenter;

import java.util.List;

public class TvRow implements IRowSignalSourceProvider {

    private final Context mContext;
    private final ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new TvCardPresenter());

    public TvRow(Context context) {
        mContext = context;
        load();
    }

    public void load() {
        for (MediaModel mediaModel : MediaModel.getDTVModels(mContext)) {
            mListRowAdapter.add(mediaModel);
        }
    }

    @Override
    public ArrayObjectAdapter getListRowAdapter() {
        return mListRowAdapter;
    }

    @Override
    public void update() {
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
