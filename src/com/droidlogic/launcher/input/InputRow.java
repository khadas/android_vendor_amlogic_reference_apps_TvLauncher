package com.droidlogic.launcher.input;

import androidx.leanback.widget.ArrayObjectAdapter;

import com.droidlogic.launcher.leanback.model.IRowSignalSourceProvider;
import com.droidlogic.launcher.leanback.presenter.content.InputSourcePresenter;

import java.util.List;

public class InputRow implements IRowSignalSourceProvider {

    private final ArrayObjectAdapter mListRowAdapter = new ArrayObjectAdapter(new InputSourcePresenter());
    private final InputSourceManager mInputSource;

    public InputRow(InputSourceManager sourceManager) {
        mInputSource = sourceManager;
        load();
    }

    public void load() {
        List<InputModel> inputModels = InputModel.getInputList(mInputSource);
        int cardCount = inputModels.size();
        for (int i = 0; i < cardCount; i++) {
            mListRowAdapter.add(inputModels.get(i));
        }
    }

    @Override
    public ArrayObjectAdapter getListRowAdapter() {
        return mListRowAdapter;
    }

    @Override
    public void update() {
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
