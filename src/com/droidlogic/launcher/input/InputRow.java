package com.droidlogic.launcher.input;

import android.content.Context;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;

import java.util.List;

public class InputRow {
    private String  mTitle;
    private Context mContext;
    private ArrayObjectAdapter mGridAdapter;
    private ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new InputCardPresenter());
    private InputSourceManager mInputSource;

    public InputRow(Context context, String title, ArrayObjectAdapter gridAdapter, InputSourceManager sourceManager){
        mContext = context;
        mTitle   = title;
        mGridAdapter = gridAdapter;
        mInputSource = sourceManager;

        load();

        HeaderItem header = new HeaderItem(0, mTitle);
        mGridAdapter.add(new ListRow(header, mListRowAdapter));
    }

    public void load(){
        List<InputModel> inputModels = InputModel.getInputList(mInputSource);
        int cardCount = inputModels.size();
        for (int i = 0; i < cardCount; i++) {
            mListRowAdapter.add(inputModels.get(i));
        }
    }

    public void update(){
        int i;

        List<InputModel> list = InputModel.getInputList(mInputSource);

        int curSize = mListRowAdapter.size();
        int newSize = list.size();
        if (curSize != newSize) {
            mListRowAdapter.clear();
            for (i = 0; i < newSize; i++) {
                InputModel model2 = (InputModel) list.get(i);
                mListRowAdapter.add(model2);
            }
        } else {
            for (i = 0; i < newSize; i++) {
                InputModel model1 = (InputModel) mListRowAdapter.get(i);
                InputModel model2 = (InputModel) list.get(i);
                if (model1.getId() != model2.getId() || model1.getIcon() != model2.getIcon()) {
                    mListRowAdapter.replace(i, model2);
                }
            }
        }
    }
}
